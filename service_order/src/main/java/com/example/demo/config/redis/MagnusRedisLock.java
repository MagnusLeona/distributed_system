package com.example.demo.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MagnusRedisLock {

    @Autowired
    public RedisTemplate redisTemplate;

    public boolean tryLock(String key) {
        try {
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(key, 1, 15, TimeUnit.MINUTES);
            return locked;
        } catch (
                Exception e) {
            return false;
        }
    }

    public void unlock(String key) {
        redisTemplate.delete(key);
    }
}
