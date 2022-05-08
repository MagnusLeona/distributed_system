package com.example.demo.magnus.service.impl;

import com.example.demo.magnus.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceImplTest {

    @Autowired
    OrderService orderService;

    @Test
    public void testCancelSuspendedOrder() {
        orderService.cancelSuspendedOrder(5L);
    }
}