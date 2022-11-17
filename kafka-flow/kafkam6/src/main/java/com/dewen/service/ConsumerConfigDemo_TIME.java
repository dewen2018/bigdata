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
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;
@Configuration
@EnableKafka
public class ConsumerConfigDemo_TIME {
 @Bean
 public Map<String, Object> consumerConfigs() {
  Map<String, Object> propsMap = new HashMap<>();
  // ---设置自动提交Offset为false
  propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
  propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "master:9092,slave1:9092,slave2:9092");
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
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        // 设置ACK模式为TIME// 每次间隔ackTime的时间提交。
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.TIME);
        // 设置提交Ack的时间间隔，单位(ms)
        factory.getContainerProperties().setAckTime(1000);
        return factory;
    }

    /**
     * -------------接收消息-------------
     */
    @KafkaListener(topics = {"test"}, groupId = "group1")
    public void kafkaListener(String message){
        System.out.println("消息："+message);
    }}