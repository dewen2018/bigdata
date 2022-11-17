package com.dewen;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 与wordcount（StreamWordCount）流程差不多
 */
public class StudentLearnStatCore {
    // private static Logger logger = Logger.getLogger(StudentLearnStatCore.class.getName());
    // private static final Logger logger = LoggerFactory.getLogger(StudentLearnStatCore.class);

    public static void main(String[] args) throws Exception {
        // logger.setLevel(Level.WARNING);
        // 定义日志级别
        Logger.getLogger("org.apache.flink").setLevel(Level.WARNING);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 使用一个自定义的source来生成数据
        DataStreamSource<String> sourceStream = env.addSource(new StudentLearnDataSource())
                // 设置并行度=1
                .setParallelism(1);

        // 数据解析
        SingleOutputStreamOperator<String> outDataStream = sourceStream
                // source端数据转换,，返回SingleOutputStreamOperator
                .map(new StudentLearnRichMapFunction())
                // 窗口1min，返回AllWindowedStream
                .windowAll(TumblingProcessingTimeWindows.of(Time.minutes(1)))
                // 汇总计算逻辑，返回SingleOutputStreamOperator
                .process(new StudentLearnRichProcessFunction());
        // 打印学生的数据
        outDataStream.print().setParallelism(1);
        env.execute("StudentLearnStat");
    }
}