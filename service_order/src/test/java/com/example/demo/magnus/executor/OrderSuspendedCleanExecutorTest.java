package com.example.demo.magnus.executor;

import org.assertj.core.api.InstantAssert;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrderSuspendedCleanExecutorTest {

    @Test
    public void testInstantNow() {
        System.out.println(Instant.now().toEpochMilli());
        System.out.println((double) Instant.now().minusMillis(60 * 1000).toEpochMilli());
        System.out.println(Long.valueOf(Instant.now().toEpochMilli()).doubleValue());
        System.out.println(Double.valueOf(Instant.now().toEpochMilli()));
        System.out.println(Double.valueOf(String.valueOf(Instant.now().toEpochMilli())));
    }

}