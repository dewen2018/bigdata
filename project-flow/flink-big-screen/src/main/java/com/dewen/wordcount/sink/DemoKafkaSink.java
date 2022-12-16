// package com.dewen.wordcount.sink;
//
// import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
// import org.apache.kafka.clients.consumer.ConsumerConfig;
//
// import java.util.Properties;
//
// public class DemoKafkaSink {
//
//     public static FlinkKafkaProducer<String> KafkaProducer(String topics) {
//         Properties p = LoadResourcesUtils.getProperties("application.properties");
//         Properties properties = new Properties();
//         properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
//         properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
//         properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, p.getProperty("spring.kafka.bootstrap-servers"));
//         properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, p.getProperty("spring.kafka.consumer.group-id"));
//         properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, p.getProperty("spring.kafka.consumer.auto-offset-reset"));
//         properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, p.getProperty("spring.kafka.consumer.enable-auto-commit"));
//         properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "org.apache.kafka.clients.consumer.RangeAssignor");
//
//         return new FlinkKafkaProducer<>(topics, new OutSerializationSchema(), properties);
//     }
// }
