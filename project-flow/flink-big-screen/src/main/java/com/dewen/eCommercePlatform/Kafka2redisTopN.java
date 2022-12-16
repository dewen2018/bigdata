package com.dewen.eCommercePlatform;

import com.alibaba.fastjson.JSON;
import com.dewen.eCommercePlatform.entity.SubOrderDetail;
import com.dewen.eCommercePlatform.ohters.MerchandiseSalesAggregateFunc;
import com.dewen.eCommercePlatform.ohters.MerchandiseSalesWindowFunc;
import com.dewen.eCommercePlatform.ohters.RankingRedisMapper;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;

import java.util.Properties;

public class Kafka2redisTopN {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        env.enableCheckpointing(60 * 1000, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointTimeout(30 * 1000);

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", MysqlCdc2Kafka.BROKER_LIST);
        props.setProperty("group.id", "flink-group");
        // props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // props.setProperty("auto.offset.reset", "latest");

        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>(MysqlCdc2Kafka.TOPIC, new SimpleStringSchema(), props);

        DataStream<String> sourceStream = env.addSource(kafkaConsumer)
                .setParallelism(1)
                .name("source_kafka_" + MysqlCdc2Kafka.TOPIC)
                .uid("source_kafka_" + MysqlCdc2Kafka.TOPIC);

        // 转换
        DataStream<SubOrderDetail> orderStream = sourceStream
                .map(message -> JSON.parseObject(message, SubOrderDetail.class))
                .name("map_sub_order_detail")
                .uid("map_sub_order_detail");

        // 窗口
        WindowedStream<SubOrderDetail, Tuple, TimeWindow> merchandiseWindowStream = orderStream.keyBy("merchandiseId")
                .window(TumblingProcessingTimeWindows.of(Time.seconds(1)));

        DataStream<Tuple2<Long, Long>> merchandiseRankStream = merchandiseWindowStream
                .aggregate(new MerchandiseSalesAggregateFunc(), new MerchandiseSalesWindowFunc())
                .name("aggregate_merch_sales")
                .uid("aggregate_merch_sales")
                .returns(TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {
                }));

        // jedis config
        FlinkJedisPoolConfig config = new FlinkJedisPoolConfig.Builder()
                .setDatabase(2)
                .setHost("127.0.0.1")
                .setPort(6379)
                .setPassword(null)
                .build();
        merchandiseRankStream.addSink(new RedisSink<>(config, new RankingRedisMapper()))
                .name("sink_merch_sales")
                .uid("sink_merch_sales")
                .setParallelism(1);

        env.execute("Kafka2redisTopN");
    }
}


