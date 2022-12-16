package com.dewen.eCommercePlatform;

import com.alibaba.fastjson.JSON;
import com.dewen.eCommercePlatform.entity.OrderAccumulator;
import com.dewen.eCommercePlatform.entity.SubOrderDetail;
import com.dewen.eCommercePlatform.ohters.GmvRedisMapper;
import com.dewen.eCommercePlatform.ohters.OrderAndGmvAggregateFunc;
import com.dewen.eCommercePlatform.ohters.OutputOrderGmvProcessFunc;
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
import org.apache.flink.streaming.api.windowing.triggers.ContinuousProcessingTimeTrigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;

import java.util.Properties;

public class Kafka2redis {

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

        WindowedStream<SubOrderDetail, Tuple, TimeWindow> siteDayWindowStream = orderStream.keyBy("siteId")
                // 窗口，时区
                .window(TumblingProcessingTimeWindows.of(Time.days(1), Time.hours(-8)))
                .trigger(ContinuousProcessingTimeTrigger.of(Time.seconds(1)));
        //  根据siteId聚合处理
        DataStream<OrderAccumulator> siteAggStream = siteDayWindowStream
                .aggregate(new OrderAndGmvAggregateFunc())
                .name("aggregate_site_order_gmv")
                .uid("aggregate_site_order_gmv");


        DataStream<Tuple2<Long, String>> siteResultStream = siteAggStream.keyBy("siteId")
                .process(new OutputOrderGmvProcessFunc(), TypeInformation.of(new TypeHint<Tuple2<Long, String>>() {

                }))
                .name("process_site_gmv_changed")
                .uid("process_site_gmv_changed");

        // jedis config
        FlinkJedisPoolConfig config = new FlinkJedisPoolConfig.Builder()
                .setDatabase(2)
                .setHost("127.0.0.1")
                .setPort(6379)
                .setPassword(null)
                .build();

        siteResultStream
                .addSink(new RedisSink<>(config, new GmvRedisMapper()))
                .name("sink_redis_site_gmv")
                .uid("sink_redis_site_gmv")
                .setParallelism(1);

        env.execute("Kafka2redis");
    }
}


