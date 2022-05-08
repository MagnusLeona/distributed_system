package com.example.demo.config.common.service;

import java.util.Optional;
import java.util.Set;

public interface RedisService {

    boolean exist(String key);

    void set(String key, String value);

    void set(String key, String value, Long timeout);

    Optional<String> get(String key);

    boolean zadd(String key, String value, double score);

    Optional<Set<String>> zrange(String key, int start, int end);

    Optional<Set<String>> zrangeByScore(String key, double start, double end);

//    void zremoveByScore(String key, double start, double end);

    boolean zremove(String key, String member);

    Boolean del(String key);

    void hset(String key, String name, String value);

    String hget(String key, String name);

    void hdel(String key, String name);

    Long increment(String key);

    void lpush(String key, String value);

    void lpushAll(String key, String... values);

    void rpush(String key, String value);

    void rpushAll(String key, String... values);

    String blpop(String key, Long timeout);

    Boolean tryLock(String key);

    Boolean tryLockWithName(String key, String name);

    Boolean unLock(String key);

    Boolean unLockWithName(String key, String name);
}
