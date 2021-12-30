package com.example.demo.magnus.mapper;

import magnus.distributed.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    public int insertPayment(Payment payment);
    public Payment selectPaymentById(Long id);
}
