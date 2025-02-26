package com.bilimili.buaa13;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableFeignClients
public class ConfigApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ConfigApplication.class, args);
        System.out.println(1);
    }
}