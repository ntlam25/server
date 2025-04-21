package com.example.crabfood_api.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .requestFactory(() -> {
                    var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout(5000);
                    factory.setReadTimeout(5000);
                    return factory;
                })
                .defaultHeader("User-Agent", "CrabFoodApp/1.0 (contact@example.com)")
                .build();
    }
}