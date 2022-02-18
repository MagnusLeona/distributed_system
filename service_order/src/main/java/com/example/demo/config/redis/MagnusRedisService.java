package com.example.demo.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MagnusRedisService {

    @Autowired
    RedisTemplate redisTemplate;

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, Object value, int timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }

    public boolean exist(String key) {
        return redisTemplate.hasKey(key);
    }
}
