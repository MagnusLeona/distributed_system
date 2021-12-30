package com.example.demo.magnus.service.impl;

import com.example.demo.magnus.mapper.PaymentMapper;
import com.example.demo.magnus.service.PaymentService;
import magnus.distributed.entity.Payment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    public PaymentMapper paymentMapper;

    @Override
    public int insertPayment(Payment payment) {
        return paymentMapper.insertPayment(payment);
    }

    @Override
    public Payment selectPaymentById(Long id) {
        return paymentMapper.selectPaymentById(id);
    }
}
