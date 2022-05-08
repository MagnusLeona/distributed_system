package com.example.demo.magnus.executor;

import com.alibaba.fastjson.JSON;
import com.example.demo.config.common.service.RedisService;
import com.example.demo.magnus.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import magnus.distributed.dict.MagnusRedisDict;
import magnus.distributed.domain.delay.queue.DelayedJob;
import magnus.distributed.entity.Order;
import magnus.distributed.executor.DelayJobHandlerExecutor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.*;

@Component
@Slf4j
public class OrderSuspendedCleanExecutor implements DelayJobHandlerExecutor, DisposableBean {

    @Value("${executor.core-pool-size}")
    public Integer corePoolSize;
    @Value("${executor.max-pool-size}")
    public Integer maxPoolSize;
    @Value("${executor.keep-alive-time}")
    public Long keepAliveTime;

    public volatile boolean destroyed = false;

    ThreadPoolExecutor threadPoolExecutor;
    ThreadPoolExecutor handleJobExecutor;

    @Autowired
    public RedisService redisService;

    @Autowired
    public OrderService orderService;

    @PostConstruct
    public void postHandle() {
        threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                Thread::new,
                new ThreadPoolExecutor.DiscardOldestPolicy());
        handleJobExecutor = new ThreadPoolExecutor(
                1,
                1,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1),
                Thread::new,
                new ThreadPoolExecutor.CallerRunsPolicy());
        handleJobExecutor.submit(this::handleDelayedJob);
    }

    @Override
    public void handleDelayedJob() {
        if (log.isDebugEnabled()) {
            log.debug("looping for expired orders");
        }
        for (; ; ) {
            // 执行逻辑
            // 每次从任务队列中获取一条记录
            try {
                if (destroyed) {
                    break;
                }
                String timeoutOrder = redisService.blpop(MagnusRedisDict.REDIS_KEY_ORDER_TOBE_WAITING_HANDLING, 2000L);
                // 正在执行的队列，如果执行完成，则走里面删除，否则超时了视为任务没有正确执行。默认执行超时时间为1分钟。
                if (Objects.isNull(timeoutOrder)) {
                    continue;
                }
                redisService.zadd(MagnusRedisDict.REDIS_KEY_SUSPENDED_ORDER_IN_EXECUTION, timeoutOrder, Instant.now().plusSeconds(60).toEpochMilli());
                if (log.isDebugEnabled()) {
                    log.debug("timeout order {} scanned", timeoutOrder);
                }
                String delayedJobJson = redisService.hget(MagnusRedisDict.REDIS_KEY_DELAYED_JOB_POOL, timeoutOrder);
                if (delayedJobJson == null) {
                    // 这里是空的？
                    Order order = orderService.selectOrderById(Long.parseLong(timeoutOrder));
                    System.out.println("----------- execution conflict order ID:  " + order.getId() + "  Status: " + order.getStatus());
                    continue;
                }
                DelayedJob delayedJob = JSON.parseObject(delayedJobJson, DelayedJob.class);
                // 修改数据库中此数据
                Future<Boolean> submit = threadPoolExecutor.submit(() ->
                        orderService.cancelSuspendedOrder(Long.valueOf(delayedJob.getJobId())));
                // 成功？删除此Job
                if (submit.get()) {
//                    log.info("timeout order {} is deleted", timeoutOrder);
                    redisService.hdel(MagnusRedisDict.REDIS_KEY_DELAYED_JOB_POOL, delayedJob.getJobId());
                    redisService.zremove(MagnusRedisDict.REDIS_KEY_SUSPENDED_ORDER_IN_EXECUTION, timeoutOrder);
                } else {
                    // 异常重试
                    // 判断当前是否超出重试次数 默认是5次
                    log.info("timeout order {} faced an error", timeoutOrder);
                    if (delayedJob.getRetry() >= 5) {
                        redisService.hdel(MagnusRedisDict.REDIS_KEY_DELAYED_JOB_POOL, delayedJob.getJobId());
                        // 持久化到数据库中，记录异常日志
                    }
                    // 没有超出重试次数，则重新放回job-pool中，并重新放到执行队列中
                    delayedJob.setRetry(delayedJob.getRetry() + 1);
                    redisService.hset(MagnusRedisDict.REDIS_KEY_DELAYED_JOB_POOL, timeoutOrder, JSON.toJSONString(delayedJob));
                    redisService.rpush(MagnusRedisDict.REDIS_KEY_ORDER_TOBE_WAITING_HANDLING, timeoutOrder);
                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setKeepAliveTime(Long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    @Override
    public void destroy() {
        this.destroyed = true;
        threadPoolExecutor.shutdown();
        handleJobExecutor.shutdown();
    }
}
