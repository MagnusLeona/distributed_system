package com.magnus.gateway;

import com.magnus.gateway.config.keyresolver.DogKeeper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
@EnableDiscoveryClient
public class MagnusApplication {

	public static void main(String[] args) {
		SpringApplication.run(MagnusApplication.class, args);
//		AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
//		annotationConfigApplicationContext.scan("com.magnus.gateway");
//		annotationConfigApplicationContext.refresh();
//		DogKeeper bean = annotationConfigApplicationContext.getBean(DogKeeper.class);
//		System.out.println(bean.toString());
	}

}
