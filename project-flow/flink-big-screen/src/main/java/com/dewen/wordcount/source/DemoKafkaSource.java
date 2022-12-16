// package com.dewen.wordcount.source;
//
// import org.apache.flink.api.common.serialization.SimpleStringSchema;
// import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
// import org.apache.kafka.clients.consumer.ConsumerConfig;
//
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Properties;
//
// public class DemoKafkaSource {
//
//     public static FlinkKafkaConsumer<String> kafkaConsumer(String topics) {
//         Properties p = LoadResourcesUtils.getProperties("application.properties");
//         Properties properties = new Properties();
//         properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//         properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//         properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, p.getProperty("spring.kafka.bootstrap-servers"));
//         properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, p.getProperty("spring.kafka.consumer.group-id"));
//         properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, p.getProperty("spring.kafka.consumer.auto-offset-reset"));
//         properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, p.getProperty("spring.kafka.consumer.enable-auto-commit"));
//         properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "org.apache.kafka.clients.consumer.RangeAssignor");
// //		String topics = consumerConfig.getTopics();
//         List<String> topicsSet = new ArrayList<String>(Arrays.asList(topics.split(",")));
//         FlinkKafkaConsumer<String> myConsumer = new FlinkKafkaConsumer<String>(topicsSet, new SimpleStringSchema(),
//                 properties);//test0是kafka中开启的topic
// //		myConsumer.assignTimestampsAndWatermarks(new CustomWatermarkEmitter());
//         return myConsumer;
//     }
// }
