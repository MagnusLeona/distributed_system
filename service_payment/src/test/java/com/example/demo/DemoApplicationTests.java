package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {


    @Autowired
    TestRestTemplate testRestTemplate;

    public int id = 0;

    @BeforeEach
    void contextLoads() {

    }

    @Test
    public void test() throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            final int t = i * 100;
            new Thread(() -> {
                for (int i1 = 1; i1 <= 100; i1++) {
                    testRestTemplate.getForEntity("http://localhost:9000/order/suspend/" + (t + i1), String.class);
                }
            }).start();
        }

        Thread.sleep(62000);

        for (int i = 0; i < 10; i++) {
            final int t = i * 100;
            new Thread(() -> {
                for (int i1 = 1; i1 <= 100; i1++) {
                    testRestTemplate.getForEntity("http://localhost:9000/order/suspend/" + (t + i1) + "/pay",
                                                  String.class);
                }
            }).start();
        }

        Thread.sleep(5000);
    }

    public HttpEntity getHttpEntity(Object o) throws JsonProcessingException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        return new HttpEntity(objectMapper.writeValueAsString(o), httpHeaders);
    }
}
