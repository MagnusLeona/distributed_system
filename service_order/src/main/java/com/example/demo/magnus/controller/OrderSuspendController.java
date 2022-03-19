package com.example.demo.magnus.controller;

import com.example.demo.magnus.service.OrderService;
import magnus.distributed.entity.Order;
import magnus.distributed.enums.OrderStatusEnum;
import magnus.distributed.response.MagnusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderSuspendController {

    @Autowired
    OrderService orderService;

    @GetMapping("order/suspend/{id}")
    public MagnusResponse<Object> suspendOrder(@PathVariable Long id) {
        // 模拟新增数据
        orderService.insertOrder(new Order(id, "123", OrderStatusEnum.CREATED.get()));
        // 模拟挂起订单
        orderService.orderSuspend(id);
        return new MagnusResponse<>(200, null, "success");
    }
}
