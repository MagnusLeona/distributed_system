package com.example.demo.magnus.controller;

import com.netflix.hystrix.*;

public class HystrixHelloWorld extends HystrixCommand<String> {

    public String name;

    public HystrixHelloWorld(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HelloWord"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("helloword"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerEnabled(true)
                        .withCircuitBreakerRequestVolumeThreshold(10)
                        .withCircuitBreakerSleepWindowInMilliseconds(5000)
                        .withCircuitBreakerErrorThresholdPercentage(50)
                        .withExecutionTimeoutEnabled(true))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(10)));
        this.name = name;
    }

    @Override
    protected String run() {
        int i = 1 / 0;
        return "hello word!" + this.name;
    }

    @Override
    protected String getFallback() {
        return "this is fallback methods";
    }

    public static void main(String[] args) {
        HystrixHelloWorld hystrixHelloWorld = new HystrixHelloWorld("Magnus");
        String execute = hystrixHelloWorld.execute();
        System.out.println(" ===== " + execute + " ===== ");
    }
}
