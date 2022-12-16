package com.dewen.wordcount;

import com.dewen.wordcount.sink.DemoRedisSink;
import com.dewen.wordcount.utils.FlinkUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;

/**
 * Created by wangyashuai on 2020/1/2.
 */
public class FlinkKafka2Redis {

    public static void main(String[] args) throws Exception {
        // args[0]
        ParameterTool parameterTool = ParameterTool.fromPropertiesFile("D:\\codes\\code2\\bigdata\\project-flow\\flink-big-screen\\src\\main\\resources\\application.properties");
        DataStream<String> kafkaStream = FlinkUtils.createKafkaStream(parameterTool, SimpleStringSchema.class);

        kafkaStream.map(new MapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(String line) throws Exception {
                        return Tuple2.of(line, 1);
                    }
                })
                // 分组
                .keyBy(0)
                .sum(1)
                .map(new MapFunction<Tuple2<String, Integer>, Tuple3<String, String, String>>() {
                    @Override
                    public Tuple3<String, String, String> map(Tuple2<String, Integer> tuple2)
                            throws Exception {
                        return Tuple3.of("WORD_COUNT", tuple2.f0, tuple2.f1.toString());
                    }
                })
                .addSink(new DemoRedisSink())
                .name("sink_wordcount")
                .uid("sink_wordcount")
                .setParallelism(1);
        FlinkUtils.getEnv().execute("FlinkKafka2Redis");
    }

}
