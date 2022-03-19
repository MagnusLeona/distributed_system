package com.example.demo.magnus.executor;

import com.example.demo.config.common.service.RedisService;
import com.example.demo.config.redis.MagnusRedisLock;
import com.example.demo.magnus.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import magnus.distributed.dict.MagnusRedisDict;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

@Component
@Slf4j
public class OrderSuspendedCleanExecutor implements InitializingBean {

    @Value("${executor.core-pool-size}")
    public Integer corePoolSize;
    @Value("${executor.max-pool-size}")
    public Integer maxPoolSize;
    @Value("${executor.keep-alive-time}")
    public Long keepAliveTime;
    @Value("${executor.period}")
    public Integer period;
    @Value("${executor.initial-delay}")
    public Integer initialDelay;

    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    public RedisService redisService;

    @Autowired
    public MagnusRedisLock magnusRedisLock;

    @Autowired
    public OrderService orderService;

    @Autowired
    public TransactionTemplate transactionTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                Thread::new,
                new ThreadPoolExecutor.DiscardOldestPolicy());
        // 开启定时任务处理无效订单检查
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> threadPoolExecutor.submit(this::loopForExpiredOrders), initialDelay, period, TimeUnit.SECONDS);
    }

    public void loopForExpiredOrders() {
        log.info("start loop for expired orders");
        // 1. 获取redis的锁
        if (magnusRedisLock.tryLock("order-hang")) {
            // 2.判断zset中是否有小于当前时间戳的时间
            try {
                // 暂定一分钟
                Optional<Set<Object>> hangedOrder = redisService.zrangeByScore(MagnusRedisDict.REDIS_KEY_ORDER_TOBE_WAITING, 0, (double) Instant.now().minusMillis(60 * 1000).toEpochMilli());
                // 3.删除已失效的订单
                hangedOrder.ifPresent(objects -> objects.forEach(o -> {
                    // 将订单状态调整为已取消
                    Long aLong = Long.valueOf(String.valueOf(o));
                    System.out.println("along " + aLong);
                    orderService.cancelSuspendedOrder((Long) o);
                    log.info("suspended order " + o + " is canceled");
                }));
            } finally {
                magnusRedisLock.unlock("order-hang");
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
}
