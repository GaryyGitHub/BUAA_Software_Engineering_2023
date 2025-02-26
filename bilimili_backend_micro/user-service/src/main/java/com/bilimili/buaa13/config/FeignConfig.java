package com.bilimili.buaa13.config;

import com.bilimili.buaa13.config.BearerTokenRequestInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor bearerTokenRequestInterceptor() {
        return new BearerTokenRequestInterceptor();
    }
}