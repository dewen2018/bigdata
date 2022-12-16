package com.dewen.eCommercePlatform;

import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Properties;

/**
 * 读取mysql日志，写入到kafka
 */
public class MysqlCdc2Kafka {
    public static final String TOPIC = "dewen1206";
    public static final String BROKER_LIST = "master:9092";
    // ,slave1:9092,slave2:9092

    public static void main(String[] args) throws Exception {
        try {
            final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            // enable checkpoint
            env.enableCheckpointing(3000);
            // source
            MySqlSource<String> mySqlSource = MySqlSource.<String>builder().hostname("192.168.111.143").port(3306)
                    // set captured database
                    .databaseList("dewen0715")
                    // set captured table
                    .tableList("dewen0715.sub_order_detail")
                    .username("root")
                    .password("Admin@123*")
                    .deserializer(new JsonDebeziumDeserializationSchema())
                    .build();

            Properties pro = new Properties();
            pro.setProperty("format", "json");
            pro.setProperty("strip_outer_array", "true");

            DataStreamSource<String> dataStreamSource = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "mysql source");

            FlinkKafkaProducer<String> producer = new FlinkKafkaProducer<>(BROKER_LIST, TOPIC, new SimpleStringSchema());
            // 逻辑处理1.map
            dataStreamSource.map(mapFunction())
                    .addSink(producer);

            dataStreamSource.setParallelism(1).print();
            env.execute("MysqlCdc2Kafka");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static MapFunction mapFunction() {
        //sink 将结果存入 kafka topic 中,存入 kafka 中的是 String 类型，所有 endResult 需要做进一步的转换
        return new MapFunction<String, String>() {
            @Override
            public String map(String line) throws Exception {
                JSONObject lineObj = JSONObject.parseObject(line);
                System.out.println(lineObj);
                JSONObject data = lineObj.getJSONObject("after");
                return data.toJSONString();
            }
        };
    }
}