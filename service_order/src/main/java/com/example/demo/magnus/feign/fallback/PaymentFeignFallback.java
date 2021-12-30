package com.example.demo.magnus.feign.fallback;

import com.example.demo.magnus.feign.service.PaymentFeignService;
import lombok.extern.slf4j.Slf4j;
import magnus.distributed.response.MagnusResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentFeignFallback implements PaymentFeignService {
    @Override
    public MagnusResponse getPaymentById(Long id) {
        log.info("服务调用失败");
        return new MagnusResponse(404, "failure", "服务调用失败");
    }
}
