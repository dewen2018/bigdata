package com.dewen.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class ProducerService {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * producer 同步方式发送数据
     *
     * @param topic   topic名称
     * @param message producer发送的数据
     */
    public void sendMessageSync(String topic, String key, String message) throws InterruptedException, ExecutionException, TimeoutException {
        //------- 方法：send(String topic, @Nullable V data)
        // 设定topic、data，向kafka发送消息
        kafkaTemplate.send(topic, message).get(10, TimeUnit.SECONDS);
        //------- 方法：send(String topic, K key, @Nullable V data)
        // // 设定topic、key、data，向kafka发送消息
        kafkaTemplate.send(topic, key, message).get(10, TimeUnit.SECONDS);
        //------- 方法：send(String topic, K key, @Nullable V data)
        // kafkaTemplate.send(topic, 0, message).get(10, TimeUnit.SECONDS);
        //------- 方法：send(String topic, Integer partition, K key, @Nullable V data)
        // 设定topic、partition、key、data，向kafka发送消息
        kafkaTemplate.send(topic, 0, key, message).get(10, TimeUnit.SECONDS);
        //------- 方法：send(String topic, Integer partition, Long timestamp, K key, @Nullable V data)
        // 设定topic、partition、timestamp、 key、data，向kafka发送消息
        kafkaTemplate.send(topic, 1, new Date().getTime(), key, message).get(10, TimeUnit.SECONDS);
        //------- 方法：send(Message<?> message)
        // 创建Spring的Message对象，然后向kafka发送消息kafkaserver不支持Magic v1 does not support record headers，服务端版本低
        // Message msg = MessageBuilder.withPayload("Send Message(payload,headers) Test")
        //         .setHeader(KafkaHeaders.MESSAGE_KEY, key)
        //         .setHeader(KafkaHeaders.TOPIC, topic)
        //         .setHeader(KafkaHeaders.PREFIX, "kafka_")
        //         .build();
        // kafkaTemplate.send(msg).get(10, TimeUnit.SECONDS);
        //------- 方法：send(ProducerRecord<K, V> record)
        // 创建ProducerRecord对象，在ProducerRecord中设置好topic、partion、key、value等信息，然后向kafka发送消息
        ProducerRecord<String, String> producerRecord1 = new ProducerRecord<>(topic, "Send ProducerRecord(topic,value) Test");
        ProducerRecord<String, String> producerRecord2 = new ProducerRecord<>(topic, "", "Send ProducerRecord(topic,key,value) Test");
        kafkaTemplate.send(producerRecord1).get(10, TimeUnit.SECONDS);
        kafkaTemplate.send(producerRecord2).get(10, TimeUnit.SECONDS);
    }

    /**
     * producer 异步方式发送数据
     *
     * @param topic   topic名称
     * @param message producer发送的数据
     */
    public void sendMessageAsync(String topic, String key, String message) {
        //------- 方法：send(String topic, @Nullable V data)
        ListenableFuture<SendResult<Integer, String>> future1 = kafkaTemplate.send(topic, message);
        //------- 方法：send(String topic, K key, @Nullable V data)
        ListenableFuture<SendResult<Integer, String>> future2 = kafkaTemplate.send(topic, key, message);
        //------- 方法：send(String topic, K key, @Nullable V data)
        // ListenableFuture<SendResult<Integer, String>> future3 = kafkaTemplate.send(topic, 0, message);
        //------- 方法：send(String topic, Integer partition, K key, @Nullable V data)
        ListenableFuture<SendResult<Integer, String>> future4 = kafkaTemplate.send(topic, 0, key, message);
        //------- 方法：send(String topic, Integer partition, Long timestamp, K key, @Nullable V data)
        ListenableFuture<SendResult<Integer, String>> future5 = kafkaTemplate.send(topic, 0, new Date().getTime(), key, message);
        //------- 方法：send(Message<?> message)
        // Message msg = MessageBuilder.withPayload("Send Message(payload,headers) Test")
        //         .setHeader(KafkaHeaders.MESSAGE_KEY, key)
        //         .setHeader(KafkaHeaders.TOPIC, topic)
        //         .setHeader(KafkaHeaders.PREFIX, "kafka_")
        //         .build();
        // ListenableFuture<SendResult<Integer, String>> future6 = kafkaTemplate.send(msg);
        //------- 方法：send(ProducerRecord<K, V> record)
        ProducerRecord<String, String> producerRecord1 = new ProducerRecord<>(topic, "Send ProducerRecord(topic,value) Test");
        ProducerRecord<String, String> producerRecord2 = new ProducerRecord<>(topic, "", "Send ProducerRecord(topic,key,value) Test");
        ListenableFuture<SendResult<Integer, String>> future7 = kafkaTemplate.send(producerRecord1);
        ListenableFuture<SendResult<Integer, String>> future8 = kafkaTemplate.send(producerRecord2);
        // 设置异步发送消息获取发送结果后执行的动作
        ListenableFutureCallback listenableFutureCallback = new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                System.out.println("success");
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("failure");
            }
        };
        // 将listenableFutureCallback与异步发送消息对象绑定
        future1.addCallback(listenableFutureCallback);
        future2.addCallback(listenableFutureCallback);
        // future3.addCallback(listenableFutureCallback);
        future4.addCallback(listenableFutureCallback);
        future5.addCallback(listenableFutureCallback);
        // future6.addCallback(listenableFutureCallback);
        future7.addCallback(listenableFutureCallback);
        future8.addCallback(listenableFutureCallback);
    }

}