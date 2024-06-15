package com.shiqu.kafka.controller;

import com.alibaba.fastjson.JSON;
import com.shiqu.kafka.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @GetMapping("/hello")
    public String hello(){
        User user = new User();
        user.setUserName("xiaowang");
        user.setAge(18);

        kafkaTemplate.send("user-topic", JSON.toJSONString(user));

        return "ok";
    }
}