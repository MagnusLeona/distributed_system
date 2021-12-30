package com.magnus.gateway.config.keyresolver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class DemoConfiguration {

    @Bean
    public DogKeeper dogKeeper(Animal dog) {
        ((Dog) dog).setName("dahuang");
        DogKeeper dogKeeper = new DogKeeper((Dog) dog);
        return dogKeeper;
    }
}
