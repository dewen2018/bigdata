//package com.dewen.kafka;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.admin.KafkaAdminClient;
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.common.KafkaFuture;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.stereotype.Component;
//import org.springframework.util.concurrent.ListenableFuture;
//import org.springframework.util.concurrent.ListenableFutureCallback;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ExecutionException;
//
//import static com.alibaba.fastjson.JSON.toJSONString;
//
///**
// * kafka生产者类
// *
// * @author kevin
// * @date 2021/7/28
// */
//@Component
//@Slf4j
//@SuppressWarnings({"unused"})
//public class ProducerUtils {
//
//    private static final String PUSH_MSG_LOG = "准备发送消息为：{}";
//    private static final String TOPIC_TEST = "flumeEvent";
//    @Autowired
//    private KafkaTemplate<String, Object> kafkaTemplate;
//
//    @Autowired
//    private KafkaAdminClient kafkaAdminClient;
//
//    /**
//     * 如果没有topic，则创建一个
//     *
//     * @param topicName    :
//     * @param partitionNum :
//     * @param replicaNum   :
//     * @return org.apache.kafka.clients.admin.CreateTopicsResult
//     * @author kevin
//     * @date 2021/8/5 9:42
//     */
//    public Boolean createTopic(String topicName, int partitionNum, int replicaNum) {
//        KafkaFuture<Set<String>> topics = kafkaAdminClient.listTopics().names();
//        try {
//            if (topics.get().contains(topicName)) {
//                return true;
//            }
//            NewTopic newTopic = new NewTopic(topicName, partitionNum, (short) replicaNum);
//            kafkaAdminClient.createTopics(Collections.singleton(newTopic));
//            return true;
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//            Thread.currentThread().interrupt();
//            return false;
//        }
//    }
//
//    /**
//     * 传入topic名称，json格式字符串的消息，生产者进行发送
//     *
//     * @param topicName : topic名称
//     * @param jsonStr   : 消息json字符串
//     * @return boolean : 推送是否成功
//     * @author kevin
//     * @date 2021/7/28 15:53
//     */
//    public boolean sendMessage(String topicName, String jsonStr) {
//        createTopic(topicName, 5, 5);
//        log.info(PUSH_MSG_LOG, jsonStr);
//        //发送消息
//        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(topicName,
//                jsonStr));
//
//        return dealSendResult(future);
//    }
//
//    /**
//     * 传入topic名称，json格式字符串数组的消息，生产者进行发送
//     *
//     * @param topicName : topic名称
//     * @param jsonStrs  : 消息json字符串数组
//     * @return boolean : 推送是否成功
//     * @author kevin
//     * @date 2021/7/28 15:53
//     */
//    public Boolean[] sendMessage(String topicName, String[] jsonStrs) {
//        createTopic(topicName, 5, 5);
//        int msgLength = jsonStrs.length;
//        Boolean[] success = new Boolean[msgLength];
//        for (int i = 0; i < msgLength; i++) {
//            String jsonStr = jsonStrs[i];
//            log.info(PUSH_MSG_LOG, jsonStr);
//            //发送消息
//            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(topicName,
//                    jsonStr));
//            success[i] = dealSendResult(future);
//        }
//        return success;
//    }
//
//    /**
//     * 传入topic名称，消息对象，生产者进行发送
//     *
//     * @param topicName : topic名称
//     * @param obj       : 消息对象
//     * @return boolean : 推送是否成功
//     * @author kevin
//     * @date 2021/7/28 15:53
//     */
//    public boolean sendMessage(String topicName, Object obj) {
//        createTopic(topicName, 5, 5);
//        String jsonStr = toJSONString(obj);
//        log.info(PUSH_MSG_LOG, jsonStr);
//        //发送消息
//        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(topicName,
//                jsonStr));
//
//        return dealSendResult(future);
//    }
//
//    /**
//     * 传入topic名称，消息对象数组，生产者进行发送
//     *
//     * @param topicName : topic名称
//     * @param list      : 消息对象数组
//     * @return boolean : 推送是否成功
//     * @author kevin
//     * @date 2021/7/28 15:56
//     */
//    public Boolean[] sendMessage(String topicName, List<Object> list) {
//        createTopic(topicName, 5, 5);
//        Boolean[] success = new Boolean[list.size()];
//        for (int i = 0; i < list.size(); i++) {
//            Object obj = list.get(i);
//            String jsonStr = toJSONString(obj);
//            log.info(PUSH_MSG_LOG, jsonStr);
//            //发送消息
//            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(topicName,
//                    jsonStr));
//            success[i] = dealSendResult(future);
//        }
//        return success;
//    }
//
//    /**
//     * 传入topic名称，json格式字符串的消息，生产者进行发送
//     *
//     * @param topicName : topic名称
//     * @param key       : 消息key
//     * @param jsonStr   : 消息json字符串
//     * @return boolean : 推送是否成功
//     * @author kevin
//     * @date 2021/7/28 15:53
//     */
//    public boolean sendMessage(String topicName, String key, String jsonStr) {
//        createTopic(topicName, 5, 5);
//        log.info(PUSH_MSG_LOG, jsonStr);
//        //发送消息
//        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(topicName,
//                key, jsonStr));
//
//        return dealSendResult(future);
//    }
//
//    /**
//     * 传入topic名称，json格式字符串数组的消息，生产者进行发送
//     *
//     * @param topicName : topic名称
//     * @param key       : 消息key
//     * @param jsonStrs  : 消息json字符串数组
//     * @return boolean : 推送是否成功
//     * @author kevin
//     * @date 2021/7/28 15:53
//     */
//    public Boolean[] sendMessage(String topicName, String key, String[] jsonStrs) {
//        createTopic(topicName, 5, 5);
//        int msgLength = jsonStrs.length;
//        Boolean[] success = new Boolean[msgLength];
//        for (int i = 0; i < msgLength; i++) {
//            String jsonStr = jsonStrs[i];
//            log.info(PUSH_MSG_LOG, jsonStr);
//            //发送消息
//            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(topicName,
//                    key, jsonStr));
//            success[i] = dealSendResult(future);
//        }
//        return success;
//    }
//
//    /**
//     * 传入topic名称，消息对象，生产者进行发送
//     *
//     * @param topicName : topic名称
//     * @param key       : 消息key
//     * @param obj       : 消息对象
//     * @return boolean : 推送是否成功
//     * @author kevin
//     * @date 2021/7/28 15:53
//     */
//    public boolean sendMessage(String topicName, String key, Object obj) {
//        createTopic(topicName, 5, 5);
//        String jsonStr = toJSONString(obj);
//        log.info(PUSH_MSG_LOG, jsonStr);
//        //发送消息
//        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(topicName,
//                key, jsonStr));
//
//        return dealSendResult(future);
//    }
//
//    /**
//     * 传入topic名称，消息对象数组，生产者进行发送
//     *
//     * @param topicName : topic名称
//     * @param key       : 消息key
//     * @param list      : 消息对象数组
//     * @return boolean : 推送是否成功
//     * @author kevin
//     * @date 2021/7/28 15:56
//     */
//    public Boolean[] sendMessage(String topicName, String key, List<Object> list) {
//        createTopic(topicName, 5, 5);
//        Boolean[] success = new Boolean[list.size()];
//        for (int i = 0; i < list.size(); i++) {
//            Object obj = list.get(i);
//            String jsonStr = toJSONString(obj);
//            log.info(PUSH_MSG_LOG, jsonStr);
//            //发送消息
//            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(topicName,
//                    key, jsonStr));
//            success[i] = dealSendResult(future);
//        }
//        return success;
//    }
//
//    /**
//     * 传入topic名称，json格式字符串的消息，生产者进行发送
//     *
//     * @param topicName : topic名称
//     * @param partition : 消息发送分区
//     * @param key       : 消息key
//     * @param jsonStr   : 消息json字符串
//     * @return boolean : 推送是否成功
//     * @author kevin
//     * @date 2021/7/28 15:53
//     */
//    public boolean sendMessage(String topicName, int partition, String key, String jsonStr) {
//        createTopic(topicName, 5, 5);
//        log.info(PUSH_MSG_LOG, jsonStr);
//        //发送消息
//        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(topicName,
//                partition, key, jsonStr));
//
//        return dealSendResult(future);
//    }
//
//    /**
//     * 传入topic名称，json格式字符串数组的消息，生产者进行发送
//     *
//     * @param topicName : topic名称
//     * @param partition : 消息发送分区
//     * @param key       : 消息key
//     * @param jsonStrs  : 消息json字符串数组
//     * @return boolean : 推送是否成功
//     * @author kevin
//     * @date 2021/7/28 15:53
//     */
//    public Boolean[] sendMessage(String topicName, int partition, String key, String[] jsonStrs) {
//        createTopic(topicName, 5, 5);
//        int msgLength = jsonStrs.length;
//        Boolean[] success = new Boolean[msgLength];
//        for (int i = 0; i < msgLength; i++) {
//            String jsonStr = jsonStrs[i];
//            log.info(PUSH_MSG_LOG, jsonStr);
//            //发送消息
//            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(topicName,
//                    partition, key, jsonStr));
//            success[i] = dealSendResult(future);
//        }
//        return success;
//    }
//
//    /**
//     * 传入topic名称，消息对象，生产者进行发送
//     *
//     * @param topicName : topic名称
//     * @param partition : 消息发送分区
//     * @param key       : 消息key
//     * @param obj       : 消息对象
//     * @return boolean : 推送是否成功
//     * @author kevin
//     * @date 2021/7/28 15:53
//     */
//    public boolean sendMessage(String topicName, int partition, String key, Object obj) {
//        createTopic(topicName, 5, 5);
//        String jsonStr = toJSONString(obj);
//        log.info(PUSH_MSG_LOG, jsonStr);
//        //发送消息
//        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(topicName,
//                partition, key, jsonStr));
//
//        return dealSendResult(future);
//    }
//
//    /**
//     * 传入topic名称，消息对象数组，生产者进行发送
//     *
//     * @param topicName : topic名称
//     * @param partition : 消息发送分区
//     * @param key       : 消息key
//     * @param list      : 消息对象数组
//     * @return boolean : 推送是否成功
//     * @author kevin
//     * @date 2021/7/28 15:56
//     */
//    public Boolean[] sendMessage(String topicName, int partition, String key, List<Object> list) {
//        createTopic(topicName, 5, 5);
//        Boolean[] success = new Boolean[list.size()];
//        for (int i = 0; i < list.size(); i++) {
//            Object obj = list.get(i);
//            String jsonStr = toJSONString(obj);
//            log.info(PUSH_MSG_LOG, jsonStr);
//            //发送消息
//            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(
//                    topicName, partition, key, jsonStr));
//            success[i] = dealSendResult(future);
//        }
//        return success;
//    }
//
//    /**
//     * 处理消息推送结果
//     *
//     * @param future :
//     * @return boolean
//     * @author kevin
//     * @date 2021/7/28 15:56
//     */
//    private boolean dealSendResult(ListenableFuture<SendResult<String, Object>> future) {
//        final boolean[] success = {false};
//        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                //发送失败的处理
//                log.info(TOPIC_TEST + " - 生产者 发送消息失败：" + throwable.getMessage());
//                success[0] = false;
//            }
//
//            @Override
//            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
//                //成功的处理
//                log.info(TOPIC_TEST + " - 生产者 发送消息成功：" + stringObjectSendResult.toString());
//                success[0] = true;
//            }
//        });
//        return success[0];
//    }
//}