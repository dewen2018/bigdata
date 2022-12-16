package com.dewen;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.Properties;

public class ReadDataFromKafka {
    public static final String TOPIC = "dewen";

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "master:9092,slave1:9092,slave2:9092");
        props.setProperty("group.id", "flink-group");
        /**
         * 第一个参数是 topic
         * 第二个参数是 value 的反序列化格式
         * 第三个参数是 kafka 配置
         */
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(TOPIC, new SimpleStringSchema(), props);

        DataStreamSource<String> stringDataStreamSource = env.addSource(consumer);
        SingleOutputStreamOperator<String> flatMap = stringDataStreamSource.flatMap(
                new FlatMapFunction<String, String>() {
                    @Override
                    public void flatMap(String s, Collector<String> outCollector) throws Exception {
                        String[] split = s.split(" ");
                        for (String currentOne : split) {
                            outCollector.collect(currentOne);
                        }
                    }
                });
        //注意这里的 tuple2 需要使用 org.apache.flink.api.java.tuple.Tuple2 这个包下的 tuple2
        SingleOutputStreamOperator<Tuple2<String, Integer>> map = flatMap.map(
                new MapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(String word) throws Exception {
                        return new Tuple2<>(word, 1);
                    }
                });
        //keyby 将数据根据 key 进行分区，保证相同的 key 分到一起，默认是按照 hash 分区
        KeyedStream<Tuple2<String, Integer>, Tuple> keyByResult = map.keyBy(0);
        WindowedStream<Tuple2<String, Integer>, Tuple, TimeWindow> windowResult =
                keyByResult.timeWindow(Time.seconds(5));
        SingleOutputStreamOperator<Tuple2<String, Integer>> endResult = windowResult.sum(1);
        //sink 直接控制台打印
        //执行 flink 程序，设置任务名称。console 控制台每行前面的数字代表当前数据是哪个并行线程计算得到的结果
        endResult.print();
        //----------------------------------------------------
        //sink 将结果存入 kafka topic 中,存入 kafka 中的是 String 类型，所有 endResult 需要做进一步的转换
        FlinkKafkaProducer<String> producer = new
                FlinkKafkaProducer<>("master:9092,slave1:9092,slave2:9092", "FlinkResult", new SimpleStringSchema());
        //将 tuple2 格式数据转换成 String 格式
        endResult.map(new MapFunction<Tuple2<String, Integer>, String>() {
            @Override
            public String map(Tuple2<String, Integer> tp2) throws Exception {
                return tp2.f0 + "-" + tp2.f1;
            }
        }).addSink(producer);
        //----------------------------------------------------
        //sink 将结果存入文件,FileSystem.WriteMode.OVERWRITE 文件目录存在就覆盖
        endResult.writeAsText("./result/kafkaresult", FileSystem.WriteMode.OVERWRITE);
        endResult.writeAsText("./result/kafkaresult", FileSystem.WriteMode.NO_OVERWRITE);
        // ---------------------------------------------------
        //最后要调用 execute 方法启动 flink 程序
        env.execute("kafka word count");
    }
}
