package com.backend.komeet.global.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient를 Bean으로 등록
 */
@Configuration
public class WebClientConfig {

    /**
     * WebClient.Builder를 Bean으로 등록
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
