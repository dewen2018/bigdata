package com.dewen.kafkaToHBase;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * 往kafka中写数据,模拟生产者
 */
@Slf4j
public class KafkaUtilsProducer {
    public static final String broker_list = "master:9092";
    //kafka topic 需要和 flink 程序用同一个 topic
    public static final String topic = "dewen";

    public static void writeToKafka() throws InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", broker_list);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer producer = new KafkaProducer<String, String>(props);
        int i = 0;
        while (true) {
            // 每隔100ms 发送一次
            Thread.sleep(100L);
            ProducerRecord record = new ProducerRecord<String, String>(
                    topic, null, null, String.valueOf(System.currentTimeMillis()));
            producer.send(record);
            log.info("record:{}", record);
            if (i % 10 == 0) {
                producer.flush();
                log.info("flush");
            }
            i++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        writeToKafka();
    }
}