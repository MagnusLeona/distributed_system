package com.example.demo.magnus.service;

import magnus.distributed.entity.Order;
import magnus.distributed.exceptions.RedisOpsException;

import java.util.Map;
import java.util.concurrent.Future;

public interface OrderService {
    int insertOrder(Order order);

    Order selectOrderById(long id);

    Future<Order> getOrder(Integer id);

    void serviceInThreadA();

    void serviceInThreadB();

    void testSemephoreIsolationA();

    void testSemephoreIsolationB();

    String testHystrixCommandFailure();

    void orderSuspend(Long id);             // 挂起订单

    boolean cancelSuspendedOrder(Long id);             // 取消挂起的订单

    boolean cancelSuspendedOrderByHand(Long id) throws Exception;

    boolean payForSuspendedOrder(Long id) throws Exception;             // 支付挂起的订单
}
