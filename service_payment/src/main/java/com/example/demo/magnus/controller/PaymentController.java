package com.example.demo.magnus.controller;

import com.example.demo.magnus.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import magnus.distributed.entity.Payment;
import magnus.distributed.response.MagnusResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
public class PaymentController {

    @Resource
    PaymentService paymentService;

    @PostMapping("payment/insert")
    public MagnusResponse insertPayment(@RequestBody Payment payment) {
        return new MagnusResponse(200, "Success", paymentService.insertPayment(payment));
    }

    @GetMapping("payment/get/{id}")
    public MagnusResponse getPayment(@PathVariable Long id) {
        log.info("[Service_Payment]::[getPayment]");
        return new MagnusResponse(200, "Success", paymentService.selectPaymentById(id));
    }
}
