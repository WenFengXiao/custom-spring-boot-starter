package com.coldcicada.app.controller;

import com.coldcicada.redisson.spring.starter.context.RedissonContext;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author coldcicada
 * @Date 2024-06-26 09:52
 * @Description
 */
@RestController
public class MultiRedissonTestController {

    @GetMapping("/stringSetTest")
    public String stringSetTest(@RequestParam("redisKey") String redisKey) {

        RedissonClient redissonClient = RedissonContext.getApplicationContext().getBean("primaryRedissonClient", RedissonClient.class);

        RBucket<String> rBucket = redissonClient.getBucket(redisKey);

        rBucket.set("hello world");

        return "ok";
    }

}
