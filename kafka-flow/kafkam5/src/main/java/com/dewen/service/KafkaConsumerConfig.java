package com.dewen.service;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String>
                factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        // 设置过滤器，只接收消息内容中包含 "test" 的消息
        RecordFilterStrategy recordFilterStrategy = new RecordFilterStrategy() {
            @Override
            public boolean filter(ConsumerRecord consumerRecord) {
                String value = consumerRecord.value().toString();
                if (value != null && value.contains("消息")) {
                    // System.err.println(consumerRecord.value());
                    // 返回 false 则接收消息
                    return false;
                }
                // 返回 true 则抛弃消息
                return true;
            }
        };
        // 将过滤器添添加到参数中
        factory.setRecordFilterStrategy(recordFilterStrategy);

        factory.getContainerProperties().setPollTimeout(3000);
        // 将单条消息异常处理器添加到参数中
        // factory.setErrorHandler(errorHandler);
        // 将批量消息异常处理器添加到参数中
        //factory.setErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

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

    /**
     * 单消息消费异常处理器
     */
    @Bean
    public ConsumerAwareListenerErrorHandler listenErrorHandler() {
        return new ConsumerAwareListenerErrorHandler() {
            @Override
            public Object handleError(Message<?> message, ListenerExecutionFailedException e, Consumer<?, ?> consumer) {
                System.out.println("message:" + message.getPayload());
                System.out.println("exception:" + e.getMessage());
                consumer.seek(new TopicPartition(message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC, String.class),
                                message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID, Integer.class)),
                        message.getHeaders().get(KafkaHeaders.OFFSET, Long.class));
                return null;
            }

        };
    }

    /**
     * 批量息消费异常处理器
     */
    // @Bean
    // public ConsumerAwareListenerErrorHandler listenErrorHandler() {
    //     return new ConsumerAwareListenerErrorHandler() {
    //
    //         @Override
    //         public Object handleError(Message<?> message,
    //                                   ListenerExecutionFailedException e,
    //                                   Consumer<?, ?> consumer) {
    //             System.out.println("message:" + message.getPayload());
    //             System.out.println("exception:" + e.getMessage());
    //             consumer.seek(new TopicPartition(message.getHeaders().get(KafkaHeaders.RECEIVED_TOPIC, String.class),
    //                             message.getHeaders().get(KafkaHeaders.RECEIVED_PARTITION_ID, Integer.class)),
    //                     message.getHeaders().get(KafkaHeaders.OFFSET, Long.class));
    //             return null;
    //         }
    //     };
    // }


    /**
     * 监听消息，接收过滤器过滤后的消息
     */
    // @KafkaListener(topics = {"topic-dewen"}, groupId = "group-dewen")
    // public void kafkaListener(String message) {
    //     System.out.println("消息：" + message);
    // }
}