package com.dewen.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    /**
     * 使用@KafkaListener注解来标记此方法为kafka消息监听器，创建消费组group1监听test topic
     *
     * @param message
     */
    @KafkaListener(topics = {"topic-dewen"}, groupId = "group-dewen", containerFactory = "kafkaListenerContainerFactory")
    public void kafkaListener(String message) {
        System.out.println("消费消息：" + message);
    }

}