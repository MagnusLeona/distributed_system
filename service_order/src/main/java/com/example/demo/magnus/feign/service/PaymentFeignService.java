package com.example.demo.magnus.feign.service;

import com.example.demo.magnus.feign.configuration.MagnusFeignClientConfiguration;
import com.example.demo.magnus.feign.fallback.PaymentFeignFallback;
import magnus.distributed.response.MagnusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "service-payment", fallback = PaymentFeignFallback.class, configuration = MagnusFeignClientConfiguration.class)
public interface PaymentFeignService {

    @GetMapping("/payment/get/{id}")
    public MagnusResponse getPaymentById(@PathVariable Long id);
}
