package com.example.demo.magnus.controller;

import com.example.demo.config.redis.MagnusRedisLock;
import com.example.demo.config.redis.MagnusRedisService;
import com.example.demo.magnus.feign.service.PaymentFeignService;
import com.example.demo.magnus.service.OrderService;
import com.netflix.discovery.converters.Auto;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import lombok.extern.slf4j.Slf4j;
import magnus.distributed.entity.Order;
import magnus.distributed.response.MagnusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class OrderController {

    @Resource
    OrderService orderService;
    @Resource
    RestTemplate restTemplate;
    @Autowired
    PaymentFeignService paymentFeignService;
    @Autowired
    MagnusRedisLock redisLock;
    @Autowired
    MagnusRedisService magnusRedisService;
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("order/get")
    public MagnusResponse getOrderById(@RequestParam Long id) {
        kafkaTemplate.send("magnus", "abc" + id);
        return new MagnusResponse(200, "success", orderService.selectOrderById(id));
    }

    @PostMapping("order/insert")
    public MagnusResponse insertOrder(@RequestBody Order order) {
        return new MagnusResponse(200, "Success", orderService.insertOrder(order));
    }

    @GetMapping("order/get/{id}")
    public MagnusResponse getOrder(@PathVariable Long id) {
        return new MagnusResponse(200, "Success", orderService.selectOrderById(id));
    }

    @GetMapping("order/cache/{id}")
    public MagnusResponse cacheOrder(@PathVariable Long id) throws InterruptedException {
        if (magnusRedisService.exist(String.valueOf(id))) {
            // 存在这个key，则直接返回
            Object o = magnusRedisService.get(String.valueOf(id));
            log.info("cache hit", o);
            return new MagnusResponse(200, "success", o);
        } else {
            // 没有值
            boolean b = redisLock.tryLock("cacheKey" + id);
            if (b) {
                Order order = orderService.selectOrderById(id);
                magnusRedisService.set(String.valueOf(id), order, 10000);
                return new MagnusResponse(200, "success", order);
            } else {
                Thread.sleep(100);
                cacheOrder(id);
            }
        }
        return null;
    }

//    @HystrixCommand(fallbackMethod = "getOrderAndPaymentFallback", commandProperties = {
//            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "10"),
//            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "50"),
//            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "5000")
//    })
    @RequestMapping("order/{orderId}/get/payment/{paymentId}")
    public MagnusResponse getOrderAndPayment(@PathVariable Long orderId, @PathVariable Long paymentId) {
        log.info("[Service_Order]::[getOrderAndPayment]");
        Map result = new HashMap();
        Map forObject = restTemplate.getForObject("http://SERVICE-PAYMENT/payment/get/" + paymentId, Map.class);
        MagnusResponse paymentById = paymentFeignService.getPaymentById(paymentId);
        Object data1 = paymentById.getData();
        HashMap data = (HashMap) forObject.get("data");
        result.put("order", orderService.selectOrderById(orderId));
        result.put("payment", data);
        result.put("feignPayment", data1);
        return new MagnusResponse(200, "Success", result);
    }

    public MagnusResponse getOrderAndPaymentFallback(@PathVariable Long orderId, @PathVariable Long paymentId) {
        return new MagnusResponse(400, "Failure", "服务异常，请稍后重试");
    }

    @RequestMapping("/testbatch")
    public MagnusResponse testBatchCommand() throws ExecutionException, InterruptedException {
        Future<Order> order = orderService.getOrder(10);
        Future<Order> order1 = orderService.getOrder(12);
        Future<Order> order2 = orderService.getOrder(13);
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(order.get());
        objects.add(order1.get());
        objects.add(order2.get());
        return new MagnusResponse(200, "OK", objects);
    }

    @RequestMapping("/thread/isolation")
    public MagnusResponse testThreadIsolation() {
        orderService.serviceInThreadA();
        orderService.serviceInThreadB();
        return new MagnusResponse(200, "OK", null);
    }

    @RequestMapping("/test/semaphore")
    public MagnusResponse testSemaphoreIsolation() {
        orderService.testSemephoreIsolationA();
        orderService.testSemephoreIsolationB();
        return new MagnusResponse(200, "OK", null);
    }

    @RequestMapping("/test/failure")
    public MagnusResponse testFailure() {
        String s = orderService.testHystrixCommandFailure();
        return new MagnusResponse(200, "OK", s);
    }
}
