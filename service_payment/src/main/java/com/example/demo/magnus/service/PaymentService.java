package com.example.demo.magnus.service;

import magnus.distributed.entity.Payment;

public interface PaymentService {
    public int insertPayment(Payment payment);
    public Payment selectPaymentById(Long id);
}
