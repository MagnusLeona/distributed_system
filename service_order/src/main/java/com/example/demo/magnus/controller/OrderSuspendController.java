package com.example.demo.magnus.controller;

import com.example.demo.magnus.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import magnus.distributed.entity.Order;
import magnus.distributed.enums.OrderStatusEnum;
import magnus.distributed.response.MagnusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 挂单---延时队列
 */
@RestController
@Slf4j
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

    @GetMapping("order/suspend/{id}/pay")
    public MagnusResponse<Object> payForSuspendedOrder(@PathVariable Long id) {
        try {
            orderService.payForSuspendedOrder(id);
            return new MagnusResponse<>(200, null, "success");
        } catch (Exception e) {
//            log.info(e.getMessage());
            return new MagnusResponse<>(500, null, "failure");
        }
    }

    @PostMapping("order/suspend/{id}/cancel")
    public MagnusResponse<Object> cancelSuspendedOrder(@PathVariable Long id) throws Exception {
        boolean b = orderService.cancelSuspendedOrderByHand(id);
        return b ? new MagnusResponse<>(200, "success", null) : new MagnusResponse<>(500, "error", null);
    }
}
