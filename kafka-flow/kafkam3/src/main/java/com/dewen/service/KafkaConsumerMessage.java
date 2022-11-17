package com.dewen.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerMessage {
    /**
     * 监听test1 topic,设置返回值为string类型，并添加@SendTo注解，将消息转发到 test2
     */
    @KafkaListener(topics = "topic-dewen", groupId = "group-dewen")
    @SendTo("topic-dewen2")
    public String kafkaListener1(String messages) {
        System.out.println(messages);
        String newMsg = messages + "消息转发测试";
        // 将处理后的消息返回
        return newMsg;
    }

    /**
     * 监听 test2 topic
     */
    @KafkaListener(topics = "topic-dewen2", groupId = "group-dewen2")
    public void kafkaListener2(String messages) {
        System.err.println(messages);
    }
}