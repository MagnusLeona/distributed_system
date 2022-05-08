package com.example.demo.magnus.executor;

import com.example.demo.config.common.service.RedisService;
import magnus.distributed.dict.MagnusRedisDict;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@SuppressWarnings("unused")
public class ExecutionTimeoutCleanExecutor {

    @Resource
    public RedisService redisService;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @PostConstruct
    public void initialize() {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "execution-timeout-clean-executor"), new ThreadPoolExecutor.DiscardPolicy());
        scheduledThreadPoolExecutor.scheduleAtFixedRate(this::detectForTimeoutExecutionSet, 1000L, 1000L, TimeUnit.MILLISECONDS);
    }

    public void detectForTimeoutExecutionSet() {
        // 检查正在执行的队列中失效的任务。
        Optional<Set<String>> strings = redisService.zrangeByScore(MagnusRedisDict.REDIS_KEY_SUSPENDED_ORDER_IN_EXECUTION, 0, Instant.now().toEpochMilli());
        strings.ifPresent(strs -> {
            String[] ss = strs.toArray(new String[]{});
            redisService.rpushAll(MagnusRedisDict.REDIS_KEY_ORDER_TOBE_WAITING_HANDLING, ss);
        });
    }

    @PreDestroy
    public void destroy() {
        this.scheduledThreadPoolExecutor.shutdown();
    }
}
