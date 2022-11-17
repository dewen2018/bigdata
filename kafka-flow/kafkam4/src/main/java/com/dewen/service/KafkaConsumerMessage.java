package com.dewen.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KafkaConsumerMessage {
    @KafkaListener(id = "id0", topicPartitions = {@TopicPartition(topic = "topic-dewen", partitions = {"0"})})
    public void listenPartition0(List<ConsumerRecord<?, ?>> records) {
        System.out.println("Id0 Listener, Thread ID: " + Thread.currentThread().getId());
        System.out.println("Id0 records size " + records.size());
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            System.out.println("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                System.out.printf(topic + " p0 Received message=" + message);
            }
        }
    }

    @KafkaListener(id = "id1", topicPartitions = {@TopicPartition(topic = "topic-dewen", partitions = {"1"})})
    public void listenPartition1(List<ConsumerRecord<?, ?>> records) {
        System.out.println("Id1 Listener, Thread ID: " + Thread.currentThread().getId());
        System.out.println("Id1 records size " + records.size());
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            System.out.println("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                System.out.printf(topic + " p1 Received message=" + message);
            }
        }
    }

    @KafkaListener(id = "id2", topicPartitions = {@TopicPartition(topic = "topic-dewen", partitions = {"2"})})
    public void listenPartition2(List<ConsumerRecord<?, ?>> records) {
        System.out.println("Id2 Listener, Thread ID: " + Thread.currentThread().getId());
        System.out.println("Id2 records size " + records.size());
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            System.out.println("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                System.out.printf(topic + " p2 Received message=" + message);
            }
        }
    }
}