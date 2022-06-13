package com.dewen.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * 批处理实现WordCount
 */
public class BatchWordCount {

    public static void main(String[] args) throws Exception {

        //初始化环境
        ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();

        //获取数据
        DataSource<String> dataSource = environment.readTextFile("/data/bigdata/word.txt");

        //聚合计算
        AggregateOperator<Tuple2<String, Integer>> operator = dataSource.flatMap(new SplitFunction()).groupBy(0).sum(1);

        operator.writeAsCsv("/data/bigdata/result").setParallelism(1);

        environment.execute("BatchWordCount");
    }

    private static class SplitFunction implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
            String[] strings = value.split(" ");

            for (String word : strings) {
                if (word.length() > 0) {
                    out.collect(new Tuple2<>(word, 1));
                }
            }
        }
    }
}
