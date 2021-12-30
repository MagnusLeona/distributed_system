package com.example.demo.magnus.service.impl;

import com.example.demo.magnus.mapper.OrderMapper;
import com.example.demo.magnus.service.OrderService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import magnus.distributed.entity.Order;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    OrderMapper orderMapper;

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
            list.add(new Order((long) i, ids.get(i).toString()));
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
}

