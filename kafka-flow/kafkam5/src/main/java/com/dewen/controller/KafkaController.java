package com.dewen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring For Kafka 提供 start()、pause() 和 resume() 方法来操作监听容器的启动、暂停和恢复。
 * start()：启动监听容器。
 * pause()：暂停监听容器。
 * resume()：恢复监听容器。
 * 这些方法一般可以灵活操作 kafka 的消费,例如进行服务进行升级，暂停消费者进行消费;
 * 例如在白天高峰期不进行服务消费，等到晚上再进行，这时候可以设置定时任务，白天关闭消费者消费到晚上开启;
 * 考虑到这些情况，利用 start()、pause()、resume() 这些方法能很好控制消费者进行消费。
 * 这里写一个简单例子，通过 cotroller 操作暂停、恢复消费者监听容器
 */
@RestController
public class KafkaController {
    @Autowired
    private KafkaListenerEndpointRegistry registry;

    /**
     * 暂停监听容器
     */
    @GetMapping("/pause")
    public void pause() {
        registry.getListenerContainer("id0").pause();
        registry.getListenerContainer("id1").pause();
    }

    /**
     * 恢复监听容器
     */
    @GetMapping("/resume")
    public void resume() {
        //判断监听容器是否启动，未启动则将其启动，否则进行恢复监听容器
        if (!registry.getListenerContainer("id0").isRunning()) {
            registry.getListenerContainer("id0").start();
        }
        registry.getListenerContainer("id0").resume();

        if (!registry.getListenerContainer("id1").isRunning()) {
            registry.getListenerContainer("id1").start();
        }
        registry.getListenerContainer("id1").resume();
    }

}