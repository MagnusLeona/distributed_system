package com.example.demo.magnus.executor;

import com.example.demo.config.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import magnus.distributed.dict.MagnusRedisDict;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

@Component
@Slf4j
public class TimeoutJobCollectorExecutor implements DisposableBean {

    @Autowired
    RedisService redisService;

    ScheduledThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    public void startLoopForCollections() {
        threadPoolExecutor = new ScheduledThreadPoolExecutor(1, Thread::new, new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPoolExecutor.scheduleAtFixedRate(this::tryToCollectTimeoutJobs, 1000L, 1000L, TimeUnit.MILLISECONDS);
    }

    public void tryToCollectTimeoutJobs() {
        // 搬运已失效的任务到执行队列中
        // 需要先获取锁
        if (redisService.tryLockWithName(MagnusRedisDict.REDIS_KEY_SUSPENDED_ORDER_LOCK, "TimeOutCollectorExecutor")) {
            try {
                Optional<Set<String>> timeoutJobs = redisService.zrangeByScore(MagnusRedisDict.REDIS_KEY_ORDER_TOBE_WAITING, 0, Instant.now().toEpochMilli());
                // 已经过期的任务
                // 将任务放到待处理的队列中
                timeoutJobs.ifPresent(jobs -> {
                    if (jobs.isEmpty()) return;
                    System.out.println("---thread: " + Thread.currentThread().getName() + "---- timeout number : " + jobs.size());
                    redisService.rpushAll(MagnusRedisDict.REDIS_KEY_ORDER_TOBE_WAITING_HANDLING, jobs.toArray(new String[]{}));
                    jobs.forEach(job -> redisService.zremove(MagnusRedisDict.REDIS_KEY_ORDER_TOBE_WAITING, job));
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisService.unLockWithName(MagnusRedisDict.REDIS_KEY_SUSPENDED_ORDER_LOCK, "TimeOutCollectorExecutor");
            }
        }
    }

    @Override
    public void destroy() {
        this.threadPoolExecutor.shutdown();
    }
}
