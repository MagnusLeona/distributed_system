package com.example.demo.magnus.service.impl;

import com.example.demo.config.common.service.RedisService;
import com.example.demo.magnus.mapper.OrderMapper;
import com.example.demo.magnus.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import lombok.extern.slf4j.Slf4j;
import magnus.distributed.dict.MagnusRedisDict;
import magnus.distributed.domain.delay.queue.DelayedJob;
import magnus.distributed.entity.Order;
import magnus.distributed.enums.OrderStatusEnum;
import magnus.distributed.exceptions.RedisOpsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    OrderMapper orderMapper;

    @Autowired
    RedisService redisService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrder(Order order) {
        return orderMapper.insertOrder(order);
    }

    @Override
    public Order selectOrderById(long id) {
        return orderMapper.selectOrderById(id);
    }

    // 请求合并
    @HystrixCollapser(batchMethod = "batchMethod", scope = com.netflix.hystrix.HystrixCollapser.Scope.GLOBAL, collapserProperties = {@HystrixProperty(name = HystrixPropertiesManager.TIMER_DELAY_IN_MILLISECONDS, value = "20"), @HystrixProperty(name = HystrixPropertiesManager.MAX_REQUESTS_IN_BATCH, value = "20")})
    @Override
    public Future<Order> getOrder(Integer id) {
        return null;
    }

    // 线程池隔离
    @HystrixCommand(threadPoolKey = "HystrixThreadA", commandProperties = {@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value = "THREAD")})
    @Override
    public void serviceInThreadA() {
        System.out.println("ServiceInThreadA:" + Thread.currentThread().getName());
    }

    @HystrixCommand(threadPoolKey = "HystrixThreadB")
    @Override
    public void serviceInThreadB() {
        System.out.println("ServiceInThreadB:" + Thread.currentThread().getName());
    }

    @HystrixCommand
    public List<Order> batchMethod(List<Integer> ids) {
        System.out.println("请求的数据：" + ids);
        List list = new ArrayList();

        for (int i = 0; i < ids.size(); i++) {
            list.add(new Order((long) i, ids.get(i).toString(), 0));
        }
        System.out.println("返回的数据：" + list);
        return list;
    }

    // 信号量隔离
    @HystrixCommand(commandProperties = {@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value = "SEMAPHORE"), @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "20")})
    public void testSemephoreIsolationA() {
        System.out.println("SemephoreIsolationA:" + Thread.currentThread().getName());
    }

    @HystrixCommand(commandProperties = {@HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value = "SEMAPHORE"), @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "20")})
    public void testSemephoreIsolationB() {
        System.out.println("SemephoreIsolationB:" + Thread.currentThread().getName());
    }

    @HystrixCommand(fallbackMethod = "failureFallback", commandProperties = {@HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "50"), @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "20"), @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "20")})
    @Override
    public String testHystrixCommandFailure() {
        int i = 1 / 0;
        return "success";
    }

    public String failureFallback() {
        System.out.println("FailureFallBack invoked");
        return "failure---command";
    }

    @Override
    public void orderSuspend(Long id) {
        // 先修改数据库
        orderMapper.updateOrderStatus(id, OrderStatusEnum.CREATED.get(), OrderStatusEnum.HANGED.get());
        // 然后放到redis任务队列中
        DelayedJob delayedJob = new DelayedJob();
        delayedJob.setJobId(String.valueOf(id));
        delayedJob.setDelayTime(3000L);
        delayedJob.setRetry(0);
        // 存放任务索引id和任务的失效时间（不同的任务可能有不同的失效时间）
        try {
            while (true) {
                if (Boolean.FALSE.equals(redisService.tryLock(MagnusRedisDict.REDIS_KEY_SUSPENDED_ORDER_LOCK))) {
                    continue;
                }
                redisService.zadd(MagnusRedisDict.REDIS_KEY_ORDER_TOBE_WAITING, String.valueOf(id),
                                  Instant.now().plusSeconds(60).toEpochMilli());
                // 存放任务的具体信息（这里其实可以简化，因为我这个业务场景并不复杂）
                ObjectMapper objectMapper = new ObjectMapper();
                redisService.hset(MagnusRedisDict.REDIS_KEY_DELAYED_JOB_POOL, delayedJob.getJobId(),
                                  objectMapper.writeValueAsString(delayedJob));
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.unLock(MagnusRedisDict.REDIS_KEY_SUSPENDED_ORDER_LOCK);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelSuspendedOrder(Long id) {
        // 先锁行数据
        Order order = orderMapper.selectOrderByIdForUpdate(id);
        if (order == null || order.getStatus().compareTo(OrderStatusEnum.HANGED.get()) == 0) {
            // 如果相等
            orderMapper.updateOrderStatus(id, OrderStatusEnum.HANGED.get(), OrderStatusEnum.CANCELED.get());
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelSuspendedOrderByHand(Long id) throws Exception {
        // 手动删除挂起的订单，需要检测是否在zset中，如果在，则删除，如果不在，则不能删除。
        boolean result = false;
        Order order = orderMapper.selectOrderByIdForUpdate(id);
        if (!Objects.isNull(order) && order.getStatus().equals(OrderStatusEnum.HANGED.get())) {
            // 如果当前订单处于挂起状态，则做相应的处理。
            orderMapper.updateOrderStatus(id, OrderStatusEnum.HANGED.get(), OrderStatusEnum.CANCELED.get());
            // 从zset中删除此订单
            for (; ; ) {
                Boolean locked = redisService.tryLock(MagnusRedisDict.REDIS_KEY_SUSPENDED_ORDER_LOCK);
                if (!locked) {
                    continue;
                }
                result = redisService.zremove(MagnusRedisDict.REDIS_KEY_ORDER_TOBE_WAITING, String.valueOf(id));
                redisService.unLock(MagnusRedisDict.REDIS_KEY_SUSPENDED_ORDER_LOCK);
                if (!result) {
                    // 如果删除失败了，说明zset中这笔订单已经失效了，需要进行回滚操作。
                    throw new Exception("order is already canceled");
                }
                break;
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payForSuspendedOrder(Long id) throws Exception {
        // 如果当前是挂起的状态，则继续执行操作
        // 需要检测zrem是否是成功操作，如果这个时候数据被转移到list中了，则这一步操作会失败，默认已经失效。
        // 加锁去修改这个值
        boolean result;
        Order order = orderMapper.selectOrderByIdForUpdate(id);
        if (!Objects.isNull(order) && order.getStatus().equals(OrderStatusEnum.HANGED.get())) {
            orderMapper.updateOrderStatus(id, OrderStatusEnum.HANGED.get(), OrderStatusEnum.PAID.get());
        }
        for (; ; ) {
            Boolean locked = redisService.tryLockWithName(MagnusRedisDict.REDIS_KEY_SUSPENDED_ORDER_LOCK,
                                                          "PayForTheOrders" + id);
            if (!locked) {
                continue;
            }
            result = redisService.zremove(MagnusRedisDict.REDIS_KEY_ORDER_TOBE_WAITING, String.valueOf(id));
            if (result) {
                redisService.hdel(MagnusRedisDict.REDIS_KEY_DELAYED_JOB_POOL, String.valueOf(id));
            }
            redisService.unLock(MagnusRedisDict.REDIS_KEY_SUSPENDED_ORDER_LOCK);
            if (!result) {
                log.debug("order is already canceled : " + id);
                throw new Exception("order is already canceled");
            }
            break;
        }
        return true;
    }
}

