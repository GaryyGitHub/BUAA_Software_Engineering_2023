package com.bilimili.buaa13;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.bilimili.buaa13.service.client")
public class VideoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoServiceApplication.class,args);
    }
}