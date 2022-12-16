package com.dewen.wordcount.utils;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @Auther dewen
 * @Date 2022年12月7日10:25:41
 */
public class FlinkUtils {
    private static final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    /***
     * 创建kafka source
     * @param parameterTool 参数
     * @param schema kafka格式
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> DataStream<T> createKafkaStream(ParameterTool parameterTool, Class<? extends DeserializationSchema> schema) throws IllegalAccessException, InstantiationException {
        // kafkaConsumer
        Properties props = new Properties();
        props.put("bootstrap.servers", parameterTool.get("bootstrap.servers"));
        props.put("group.id", parameterTool.get("group.id"));
        props.put("enable.auto.commit", parameterTool.get("enable.auto.commit", "false"));
        // props.setProperty("auto.offset.reset", "latest");
        props.put("auto.offset.reset", parameterTool.get("auto.offset.reset", "earliest"));
        // props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        String topics = parameterTool.getRequired("topics");
        List<String> tpoicLIst = Arrays.asList(topics.split(","));
        FlinkKafkaConsumer<T> kafkaConsumer = new FlinkKafkaConsumer<T>(tpoicLIst, schema.newInstance(), props);

        //  checkPoint：默认10s
        env.enableCheckpointing(parameterTool.getLong("checkpoint.interval", 10000), CheckpointingMode.EXACTLY_ONCE);
        //  取消任务,checkpoint不删除
        env.getCheckpointConfig().enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION);
        //  配置全局参数
        env.getConfig().setGlobalJobParameters(parameterTool);


        return env.addSource(kafkaConsumer)
                .setParallelism(1)
                .name("source_kafka_" + topics)
                .uid("source_kafka_" + topics);
    }

    /**
     * 获取环境
     *
     * @return
     */
    public static StreamExecutionEnvironment getEnv() {
        return env;
    }

}
