package com.example.demo.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MagnusRedisLock {

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    public boolean tryLock(String key) {
        // Redis单机锁，redission项目开发者建议的使用方式，较为简单，但是非强一致性
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, 1, 15, TimeUnit.SECONDS));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean unlock(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            return unlock(key);
        }
    }
}
