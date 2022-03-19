package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.Set;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        Boolean orderToBeWaiting1 = redisTemplate.opsForZSet().add("ac", 123, 123);
        Set orderToBeWaiting = redisTemplate.opsForZSet().range("ac", 0, 10);
        Set orderToBeWaiting2 = redisTemplate.opsForZSet().range("orderToBeWaiting", 0, -1);
        Set orderToBeWaiting3 = redisTemplate.opsForZSet().rangeByScore("orderToBeWaiting", 0, (double) Instant.now().toEpochMilli());
        System.out.println("--------------------");
        orderToBeWaiting.forEach(System.out::println);
        System.out.println("--------------------");
        orderToBeWaiting2.forEach(System.out::println);
        System.out.println("--------------------");
        orderToBeWaiting3.forEach(System.out::println);
        System.out.println("--------------------");
    }

}
