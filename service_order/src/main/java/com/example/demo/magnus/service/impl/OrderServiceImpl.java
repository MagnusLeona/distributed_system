package com.example.demo.magnus.service.impl;

import com.example.demo.config.common.service.RedisService;
import com.example.demo.magnus.mapper.OrderMapper;
import com.example.demo.magnus.service.OrderService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import magnus.distributed.dict.MagnusRedisDict;
import magnus.distributed.entity.Order;
import magnus.distributed.enums.OrderStatusEnum;
import magnus.distributed.exceptions.RedisOpsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    OrderMapper orderMapper;

    @Autowired
    RedisService redisService;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Override
    public int insertOrder(Order order) {
        return orderMapper.insertOrder(order);
    }

    @Override
    public Order selectOrderById(long id) {
        return orderMapper.selectOrderById(id);
    }

    // 请求合并
    @HystrixCollapser(batchMethod = "batchMethod", scope = com.netflix.hystrix.HystrixCollapser.Scope.GLOBAL, collapserProperties = {
            @HystrixProperty(name = HystrixPropertiesManager.TIMER_DELAY_IN_MILLISECONDS, value = "20"),
            @HystrixProperty(name = HystrixPropertiesManager.MAX_REQUESTS_IN_BATCH, value = "20")
    })
    @Override
    public Future<Order> getOrder(Integer id) {
        return null;
    }

    // 线程池隔离
    @HystrixCommand(threadPoolKey = "HystrixThreadA", commandProperties = {
            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value = "THREAD")
    })
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
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value = "SEMAPHORE"),
            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "20")
    })
    public void testSemephoreIsolationA() {
        System.out.println("SemephoreIsolationA:" + Thread.currentThread().getName());
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value = "SEMAPHORE"),
            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "20")
    })
    public void testSemephoreIsolationB() {
        System.out.println("SemephoreIsolationB:" + Thread.currentThread().getName());
    }

    @HystrixCommand(fallbackMethod = "failureFallback", commandProperties = {
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "50"),
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "20"),
            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "20")
    })
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
    @Transactional
    public void orderSuspend(Long id) {
        // 先修改数据库
        orderMapper.updateOrderStatus(id, OrderStatusEnum.CREATED.get(), OrderStatusEnum.HANGED.get());
        // 然后放到redis任务队列中
        redisService.zadd(MagnusRedisDict.REDIS_KEY_ORDER_TOBE_WAITING, id, (double) Instant.now().toEpochMilli());
        // todo: 使用的是系统时间戳，那么就和当前服务器的时间绑定了，那么分布式架构层面上就一定会出现时钟不同步的问题。
        // fixme: 怎么解决？
    }

    @Override
    @Transactional
    public void cancelSuspendedOrder(Long id) {
        // 先锁行数据
        Order order = orderMapper.selectOrderByIdForUpdate(id);
        if (order.getStatus().compareTo(OrderStatusEnum.HANGED.get()) == 0) {
            // 如果相等
            orderMapper.updateOrderStatus(id, OrderStatusEnum.HANGED.get(), OrderStatusEnum.CANCELED.get());
        }
        // 如果以上步骤均无问题，则从挂起的订单列表删除此订单
        redisService.zremove(MagnusRedisDict.REDIS_KEY_ORDER_TOBE_WAITING, String.valueOf(id));
    }

    @Override
    @Transactional
    public boolean payForSuspendedOrder(Long id) {
        // 支付挂起的订单
        // 需要放到事务中去执行
        Order order = orderMapper.selectOrderByIdForUpdate(id);
        // select for update将会锁行数据
        // todo: 是否可以考虑只返回status
        if (order.getStatus().equals(OrderStatusEnum.HANGED.get())) {
            // 如果当前是挂起的状态，则继续执行操作
            orderMapper.updateOrderStatus(id, OrderStatusEnum.HANGED.get(), OrderStatusEnum.PAID.get());
            return true;
        }
        // 否则呢？
        return false;
    }
}

