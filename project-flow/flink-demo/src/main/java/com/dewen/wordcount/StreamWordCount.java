package com.dewen.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 流处理实现WordCount
 */
public class StreamWordCount {

    public static void main(String[] args) throws Exception {
        //连接端口号
        // final int port;
        // try {
        //     final ParameterTool params = ParameterTool.fromArgs(args);
        //     port = params.getInt("port");
        // } catch (Exception e) {
        //     System.out.println("No port specified. Please run 'SocketWindowWordCount --port <port>'");
        //     return;
        // }
        Logger.getLogger("org.apache.flink").setLevel(Level.WARNING);

        //获取执行环节
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //获取连接socket输入数据
        DataStreamSource<String> data = env.socketTextStream("master", 9999, "\n");

        //解析数据、对数据进行分组、窗口函数和统计个数
        SingleOutputStreamOperator<WordWithCount> pairWords = data.flatMap(new FlatMapFunction<String, WordWithCount>() {

            private static final long serialVersionUID = 6800597108091365154L;

            public void flatMap(String value, Collector<WordWithCount> out) throws Exception {
                for (String word : value.split("//s")) {
                    out.collect(new WordWithCount(word, 1L));
                }
            }
        });
        //将元组按照key进行分组
        KeyedStream<WordWithCount, Tuple> grouped = pairWords.keyBy("word");
        //窗口操作，参数：窗口长度和滑动间隔
        WindowedStream<WordWithCount, Tuple, TimeWindow> window = grouped.timeWindow(Time.seconds(10), Time.seconds(2));
        // SingleOutputStreamOperator<WordWithCount> counts = window.sum("count");
        // 或者下边的计算
        SingleOutputStreamOperator<WordWithCount> windowCounts = window.reduce(new ReduceFunction<WordWithCount>() {
            public WordWithCount reduce(WordWithCount value1, WordWithCount value2) throws Exception {
                return new WordWithCount(value1.word, value1.count + value2.count);
            }
        });
        //打印
        windowCounts.print().setParallelism(1);
        env.execute("Socket Window WordCount");
    }

}
