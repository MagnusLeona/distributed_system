package com.example.demo.config.common.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RedisService {
    void set(String key, Object value);

    Optional<Object> get(String key);

    boolean zadd(String key, Object value, double score);

    Optional<Set<Object>> zrange(String key, int start, int end);

    Optional<Set<Object>> zrangeByScore(String key, double start, double end);

    void zremove(String key, String member);

    Boolean del(String key);

    Long increment(String key);

    Boolean tryLock(String key, double value);
}
