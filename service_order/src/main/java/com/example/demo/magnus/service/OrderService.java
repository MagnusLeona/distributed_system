package com.example.demo.magnus.service;

import magnus.distributed.entity.Order;

import java.util.concurrent.Future;

public interface OrderService {
    public int insertOrder(Order order);
    public Order selectOrderById(long id);
    public Future<Order> getOrder(Integer id);
    public void serviceInThreadA();
    public void serviceInThreadB();
    public void testSemephoreIsolationA();
    public void testSemephoreIsolationB();
    public String testHystrixCommandFailure();
}
