package com.example.demo.magnus.executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderHangedCleanExecutorTest {

    private OrderSuspendedCleanExecutor orderHangedCleanExecutor;

    @BeforeEach
    public void setUp() {
        orderHangedCleanExecutor = new OrderSuspendedCleanExecutor();
        orderHangedCleanExecutor.setCorePoolSize(1);
        orderHangedCleanExecutor.setKeepAliveTime(5L);
        orderHangedCleanExecutor.setMaxPoolSize(5);
    }

    @Test
    public void shouldExecuteJobsInternally() throws Exception {
        // 开启定时任务
        orderHangedCleanExecutor.afterPropertiesSet();
    }

}