package com.dewen.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * 为了使创建 kafka 监听器更加简单，Spring For Kafka 提供了 @KafkaListener 注解，
 * 该 @KafkaListener 注解配置方法上，凡是带上此注解的方法就会被标记为是 Kafka 消息监听器，
 * 所以可以用 @KafkaListener 注解快速创建消息监听器。
 */
@Configuration
@EnableKafka
public class ConsumerConfigDemo {
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "master:9092,slave1:9092,slave2:9092");
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return propsMap;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // 创建3个线程并发消费
        factory.setConcurrency(3);
        // 设置拉取数据超时时间
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    /**
     *
     * 监听单个 Topic 示例
     * ---使用@KafkaListener注解来标记此方法为kafka消息监听器，创建消费组group1监听test topic
     */
    // @KafkaListener(topics = {"topic-dewen"}, groupId = "group-dewen")
    // public void kafkaListener(String message) {
    //     System.out.println("消息：" + message);
    // }

    /**
     * 监听多个 Topic 示例
     *
     * @param message
     */
    // @KafkaListener(topics = {"topic-dewen", "topic-dewen2"}, groupId = "group-dewen")
    // public void kafkaListener(String message) {
    //     System.out.println("消息：" + message);
    // }

    /**
     * 监听某个 Topic 的某个分区示例
     *
     * @param message
     */
    // @KafkaListener(id = "id0", groupId = "group-dewen", topicPartitions = {@TopicPartition(topic = "topic-dewen", partitions = {"0"})})
    // public void kafkaListener1(String message) {
    //     System.out.println("消息0partition：" + message);
    // }
    //
    // @KafkaListener(id = "id1", groupId = "group-dewen", topicPartitions = {@TopicPartition(
    //         topic = "topic-dewen", partitions = {"1", "2"})}
    //         // concurrency就是同组下的消费者个数，就是并发消费数，建议小于等于分区总数
    //         // , concurrency = "1"
    // )
    // public void kafkaListener2(String message) {
    //     System.out.println("消息1,2partition：" + message);
    // }

    /**
     * 监听多个 Topic 的分区示例
     *
     * @param message
     */
    // @KafkaListener(id = "id3", groupId = "group-dewen", topicPartitions = {
    //         @TopicPartition(topic = "topic-dewen2", partitions = {"0"}),
    //         @TopicPartition(topic = "topic-dewen", partitions = {"0", "1", "2"})
    // })
    // public void kafkaListener(String message) {
    //     System.out.println(message);
    // }

    /**
     * 监听多个 Topic 的分区示例
     *
     * @param message
     */
    // @KafkaListener(topics = "topic-dewen", groupId = "group-dewen")
    // public void kafkaListener(@Payload String message,
    //                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
    //                           @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
    //     System.out.println("主题:" + topic);
    //     System.out.println("键key:" + key);
    //     System.out.println("消息:" + message);
    // }

    /**
     * 监听 topic 进行批量消费
     */
    // @KafkaListener(topics = "topic-dewen", groupId = "group-dewen")
    // public void kafkaListener(List<String> messages) {
    //     for (String msg : messages) {
    //         System.out.println(msg);
    //     }
    // }

    /**
     * 监听 topic 并手动提交 Offset 偏移量
     * 如果设置为手动提交 Offset 偏移量，并且设置 Ack 模式为 MANUAL 或 MANUAL_IMMEDIATE,
     * 则需要在方法参数中引入 Acknowledgment 对象，并执行它的 acknowledge() 方法来提交偏移量。
     */
    // @KafkaListener(topics = "topic-dewen", groupId = "group-dewen")
    // public void kafkaListener(List<String> messages, Acknowledgment acknowledgment) {
    //     for (String msg : messages) {
    //         System.out.println(msg);
    //     }
    //     // 触发提交offset偏移量
    //     acknowledgment.acknowledge();
    // }

    /**
     * 模糊匹配多个 Topic
     * 可以添加参数 topicPattern ，输入通配符来对多个 topic 进行监听，
     * 例如这里使用 “test.*” 将监听所有以 test 开头的 topic 的消息。
     */
    @KafkaListener(topicPattern = "topic-.*", groupId = "group-dewen")
    public void annoListener2(String messages) {
        System.err.println(messages);
    }
}