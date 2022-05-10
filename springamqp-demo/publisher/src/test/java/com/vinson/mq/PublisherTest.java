package com.vinson.mq;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PublisherTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 一次发送1条消息
     */
    @Test
    public void testSimpleQueue(){
        String queueName ="simple.queue";
        String message ="Hello Spring amqp";
        rabbitTemplate.convertAndSend(queueName, message);
    }

    /**
     * 一次性发送50条消息
     */
    @Test
    public void testSimpleQueue2() throws InterruptedException{
        String queueName = "simple.queue";
        String message = "hello, Message__";
        for(int i=1; i<=50; i++){
            rabbitTemplate.convertAndSend(queueName, message + i);
            Thread.sleep(20);
        }
    }

    @Test
    public void testSendFanoutExchange(){
        //交换机名字
        String exchangeName = "vinson.fanout";
        //消息
        String message = "hello everyone";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName,"",message);
    }


    @Test
    public void testSendDirectExchange(){
        //交换机名字
        String exchangeName = "vinson.direct";
        //消息
        String message = "hello, blue";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName,"red",message);
    }

    @Test
    public void testSendTopicExchange(){
        //交换机名字
        String exchangeName = "vinson.topic";
        //消息
        String message = "传智教育在深交所上市了";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName,"china.weather",message);
    }

    @Test
    public void testSendObject(){
        Map<String, Object> msg = new HashMap<>();
        msg.put("name", "vinson");
        msg.put("age", 35);
        rabbitTemplate.convertAndSend("object.queue", msg);
    }
}
