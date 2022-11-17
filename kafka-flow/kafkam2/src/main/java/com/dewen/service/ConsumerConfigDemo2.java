// package com.dewen.service;
//
// import org.apache.kafka.clients.consumer.ConsumerConfig;
// import org.apache.kafka.clients.consumer.ConsumerRecord;
// import org.apache.kafka.common.serialization.StringDeserializer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.annotation.EnableKafka;
// import org.springframework.kafka.core.ConsumerFactory;
// import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
// import org.springframework.kafka.listener.BatchMessageListener;
// import org.springframework.kafka.listener.ContainerProperties;
// import org.springframework.kafka.listener.KafkaMessageListenerContainer;
//
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// /**
//  * Spring For Kafka 提供了消息监听器接口的两种实现类，分别是：
//  * 1.KafkaMessageListenerContainer 利用单个线程来接收全部主题中全部分区上的所有消息。
//  * 2.ConcurrentMessageListenerContainer 代理的一个或多个 KafkaMessageListenerContainer 实例，来实现多个线程消费。
//  * 下列为： KafkaMessageListenerContainer 实例来监听 Kafka 消息
//  */
// @Configuration
// @EnableKafka
// public class ConsumerConfigDemo2 {
//     @Bean
//     public Map<String, Object> consumerConfigs() {
//         Map<String, Object> propsMap = new HashMap<>();
//         propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "master:9092,slave1:9092,slave2:9092");
//         // 自动提交
//         propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
//         propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
//         propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//         propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//         return propsMap;
//     }
//
//     @Bean
//     public ConsumerFactory<Integer, String> consumerFactory() {
//         return new DefaultKafkaConsumerFactory<>(consumerConfigs());
//     }
//
//     /**
//      * 创建 KafkaMessageListenerContainer 实例监听 kafka 消息
//      */
//     @Bean
//     public KafkaMessageListenerContainer demoListenerContainer() {
//         // 创建container配置参数，并指定要监听的 topic 名称
//         ContainerProperties properties = new ContainerProperties("topic-dewen");
//         // 设置消费者组名称
//         properties.setGroupId("group-dewen");
//         // 设置监听器监听 kafka 消息
//         // properties.setMessageListener(new MessageListener<Integer, String>() {
//         //     @Override
//         //     public void onMessage(ConsumerRecord<Integer, String> record) {
//         //         System.out.println("消息：" + record);
//         //     }
//         // });
//         properties.setMessageListener(new BatchMessageListener<Integer, String>() {
//             @Override
//             public void onMessage(List<ConsumerRecord<Integer, String>> list) {
//                 for (ConsumerRecord<Integer, String> record : list) {
//                     System.out.println("消息：" + record);
//                 }
//             }
//         });
//         // properties.setMessageListener(new AcknowledgingConsumerAwareMessageListener<Integer, String>() {
//         //     @Override
//         //     public void onMessage(ConsumerRecord<Integer, String> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
//         //     }
//         // });
//         // properties.setMessageListener(new BatchAcknowledgingConsumerAwareMessageListener<Integer, String>() {
//         //     @Override
//         //     public void onMessage(List<ConsumerRecord<Integer, String>> list, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
//         //     }
//         // });
//         return new KafkaMessageListenerContainer(consumerFactory(), properties);
//     }
// }