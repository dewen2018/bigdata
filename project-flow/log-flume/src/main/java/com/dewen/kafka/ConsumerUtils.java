//package com.dewen.kafka;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.annotation.TopicPartition;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.config.KafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.listener.ContainerProperties;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
///**
//  * kafka消费者类
//  * @author kevin
//  * @date 2021/7/28
//  */
//@Component
//@Slf4j
//public class ConsumerUtils {
//
//    @Autowired
//    private RedisUtils redisUtils;
//
//    @Bean
//    public KafkaListenerContainerFactory<?> batchFactory(ConsumerFactory consumerFactory){
//        ConcurrentKafkaListenerContainerFactory<Integer,String> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory);
//        factory.setConcurrency(5);
//        factory.getContainerProperties().setPollTimeout(1000);
//        factory.setBatchListener(true);//设置为批量消费，每个批次数量在Kafka配置参数中设置
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);//设置手动提交ackMode
//        return factory;
//    }
//
//    /**
//      * 单条的消费kafka消息
//      * @author kevin
//      * @param record : 消息记录
//      * @param ack : ack回调确认
//      * @return void :
//      * @date 2021/8/3 15:14
//      */
//    @KafkaListener(topics = KafkaConstants.TOPIC_TEST, topicPartitions = {
//            @TopicPartition(topic = KafkaConstants.TOPIC_TEST, partitions = {"0" ,"2" ,"4"}),
//    }, groupId = KafkaConstants.TOPIC_GROUP1)
//    public void topicTest(ConsumerRecord<String, String> record, Acknowledgment ack) {
//
//        Optional<String> message = Optional.ofNullable(record.value());
//        if (message.isPresent()) {
//            Object msg = message.get();
//            log.info("topic_test 消费了： Topic:" + record.topic() + ",key:" + record.key() + ",Message:" + msg);
//            ack.acknowledge();//手动提交offset
//        }
//    }
//
//    /**
//      * 批量的消费kafka消息，要配合containerFactory使用，配置的bean见batchFactory
//      * @author kevin
//      * @param records : 消息记录列表
//      * @param ack : ack回调确认
//      * @return void :
//      * @date 2021/8/3 15:15
//      */
//    @Transactional(rollbackOn = Exception.class)
//    @KafkaListener(topics = KafkaConstants.TOPIC_TEST, topicPartitions = {
//            @TopicPartition(topic = KafkaConstants.TOPIC_TEST, partitions = {"1", "3"}),
//    }, groupId = KafkaConstants.TOPIC_GROUP2, containerFactory="batchFactory")
//    public void topicTest2(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
//        try {
//            for (ConsumerRecord<String, String> record : records) {
//                //取到消息后，先查询缓存中是否已经存在，存在表示不需要再次处理
//                //如果消息不存在，业务处理完成后将消息存入redis；如果消息存在直接跳过；这样可防止重复消费
//                boolean isExists = redisUtils.hasKey(record.topic() + record.partition() + record.key());
//                if (!isExists) {
//                    Optional<String> message = Optional.ofNullable(record.value());
//                    if (message.isPresent()) {
//                        Object msg = message.get();
//                        log.info("topic_test1 消费了： Topic:" + record.topic() + ",key:" + record.key() + ",Message:" + msg);
//                    }
//                    redisUtils.set(record.topic() + record.partition() + record.key(), record.value());
//                }
//            }
//            ack.acknowledge();//手动提交offset
//        }catch (Exception e){
//            log.error(e.getMessage());
//            throw e;
//        }
//    }
//}