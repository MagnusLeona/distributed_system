package com.example.demo;

import com.example.demo.config.common.service.RedisService;
import com.example.demo.magnus.service.OrderService;
import magnus.distributed.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.*;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Test
    void contextLoads() {
        redisTemplate.opsForZSet().add("ac", "123", 123);
        Set<String> orderToBeWaiting = redisTemplate.opsForZSet().range("ac", 0, 10);
        redisTemplate.opsForZSet().remove("ac", "123");
        Set<String> result = redisTemplate.opsForZSet().range("ac", 0, -1);
        orderToBeWaiting.forEach(System.out::println);
        result.forEach(System.out::println);
    }

    @Test
    void setIfAbsentValue() {
        Boolean abc = redisService.tryLock("abc");
        System.out.println(abc);
        Boolean abc1 = redisService.tryLock("abc");
        System.out.println(abc1);
    }

    @Test
    void givenIdTobeDeleteShouldDeleteCorrectly() {
        redisTemplate.opsForZSet().add("ac", "123", 123);
        Long ac = redisTemplate.opsForZSet().remove("ac", 123);
        Set<String> ac1 = redisTemplate.opsForZSet().range("ac", 0, -1);
        assert Objects.requireNonNull(ac1).isEmpty();
    }

    @Test
    void givenRedisServiceShouldExecuteCorrectly() {
        redisService.zadd("ac", "123", 123);
        Optional<Set<String>> ac = redisService.zrange("ac", 0, -1);
        assert ac.isPresent();
        redisService.zremove("ac", "123");
        Optional<Set<String>> ac1 = redisService.zrange("ac", 0, -1);
        assert ac1.isEmpty() || ac1.get().isEmpty();
    }

    @Test
    void testSetToArray() {
        TreeSet<String> objects = new TreeSet<>();
        objects.add("abc");
        objects.add("def");

        String[] strings = objects.toArray(new String[]{});
        System.out.println(strings);
        Arrays.stream(strings).forEach(System.out::println);

        String abc = redisTemplate.opsForList().leftPop("abc");
        Long remove = redisTemplate.opsForZSet().remove("abc", "a");
    }

    @Test
    void testRedisTemplateHandleZsetRemWithInvalidKey() {
        redisTemplate.opsForZSet().add("abc", "1", 1);
        Long abc = redisTemplate.opsForZSet().remove("abc", "1");
        System.out.println("---------------------------");
        System.out.println(abc);


        System.out.println(Long.compare(1L, (long) 1.0));
    }

    @Test
    void testDistributedLockCanCorrectlyWorking() {
        Boolean abc = redisService.tryLock("abc");
        Boolean abc1 = redisService.tryLock("abc");
        System.out.println(abc == abc1);
        System.out.println("===================================");
    }

    @Test
    void testRollBack() throws Exception {
        Order order = new Order(1L, "abc", 1);
        orderService.insertOrder(order);
        throw new Exception();
    }
}
