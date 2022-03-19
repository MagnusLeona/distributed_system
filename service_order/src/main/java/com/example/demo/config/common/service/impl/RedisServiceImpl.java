package com.example.demo.config.common.service.impl;

import com.example.demo.config.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class RedisServiceImpl implements RedisService {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Optional<Object> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public boolean zadd(String key, Object value, double score) {
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
    }

    @Override
    public Optional<Set<Object>> zrange(String key, int start, int end) {
        return Optional.ofNullable(redisTemplate.opsForZSet().range(key, start, end));
    }

    @Override
    public Optional<Set<Object>> zrangeByScore(String key, double start, double end) {
        return Optional.ofNullable(redisTemplate.opsForZSet().rangeByScore(key, start, end));
    }

    @Override
    public void zremove(String key, String member) {
        redisTemplate.opsForZSet().remove(key, member);
    }

    @Override
    public Boolean del(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @Override
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    @Override
    public Boolean tryLock(String key, double value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }
}
