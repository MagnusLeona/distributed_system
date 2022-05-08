package com.example.demo.config.common.service.impl;

import com.example.demo.config.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisServiceImpl implements RedisService {
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean exist(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, Long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public boolean zadd(String key, String value, double score) {
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
    }

    @Override
    public Optional<Set<String>> zrange(String key, int start, int end) {
        return Optional.ofNullable(redisTemplate.opsForZSet().range(key, start, end));
    }

    @Override
    public Optional<Set<String>> zrangeByScore(String key, double start, double end) {
        return Optional.ofNullable(redisTemplate.opsForZSet().rangeByScore(key, start, end));
    }

    @Override
    public boolean zremove(String key, String member) {
        Long remove = redisTemplate.opsForZSet().remove(key, member);

        if (remove == null) {
            return false;
        }
        return remove > 0;
    }

    @Override
    public Boolean del(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @Override
    public void hset(String key, String name, String value) {
        redisTemplate.opsForHash().put(key, name, value);
    }

    @Override
    public String hget(String key, String name) {
        return (String) redisTemplate.opsForHash().get(key, name);
    }

    @Override
    public void hdel(String key, String name) {
        redisTemplate.opsForHash().delete(key, name);
    }

    @Override
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    @Override
    public void lpush(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public void lpushAll(String key, String... values) {
        redisTemplate.opsForList().leftPushAll(key, values);
    }

    @Override
    public void rpush(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public void rpushAll(String key, String... values) {
        redisTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    public String blpop(String key, Long timeout) {
        return redisTemplate.opsForList().leftPop(key, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public Boolean tryLock(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(1), 1000, TimeUnit.SECONDS));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean tryLockWithName(String key, String name) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, name, 100000000, TimeUnit.SECONDS));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean unLock(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            return unLock(key);
        }
    }

    @Override
    public Boolean unLockWithName(String key, String name) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            return unLock(key);
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("redisService shutting down");
    }
}
