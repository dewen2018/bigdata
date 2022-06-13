package com.dewen.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Component
public class Producer {
    @Autowired
    private KafkaTemplate<Object, Object> template;

    @GetMapping("/send/{input}")
    public void sendFoo(@PathVariable String input) {
        this.template.send("flumeEvent", input);
    }

    @KafkaListener(groupId = "flumeEvent", topics = "flumeEvent")
    public void listen(String input) {
        System.out.println(input);
//        log.info("input value: {}", input);
    }


//    @GetMapping(value = "/test2")
//    public ResponseVo test2(String value, String key, Integer partition){
//        if(StringUtils.isBlank(value)){
//            return new ResponseVo.Builder().error().message("请从传入发送的消息！").build();
//        }
//        Message message = new Message.Builder().id(UuidUtil.getUuid32()).msg(value).sendTime(DateUtils.nowDate()).build();
//        String str = JSONObject.toJSONString(message);
//        if(StringUtils.isNotBlank(key)){
//            if(partition != null){
//                producerUtils.sendMessage(KafkaConstants.TOPIC_TEST, partition, key, str);
//            }else{
//                producerUtils.sendMessage(KafkaConstants.TOPIC_TEST, key, str);
//            }
//        }else {
//            producerUtils.sendMessage(KafkaConstants.TOPIC_TEST, str);
//        }
//        return null;
//    }
}
