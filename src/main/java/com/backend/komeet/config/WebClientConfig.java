package com.backend.komeet.config;

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
     *
     * @return WebClient.Builder 인스턴스
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
