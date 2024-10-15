package com.backend.immilog.global.configuration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@DisplayName("RestTemplateConfig 클래스 테스트")
class RestTemplateConfigTest {
    private final RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
    private final RestTemplateConfig restTemplateConfig = new RestTemplateConfig();

    @Test
    @DisplayName("RestTemplate 객체가 정상적으로 생성되는지 테스트")
    void restTemplateIsNotNull() {
        when(restTemplateBuilder.setConnectTimeout(Duration.ofMillis(5000))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.setReadTimeout(Duration.ofMillis(5000))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(mock(RestTemplate.class));

        RestTemplate restTemplate = restTemplateConfig.restTemplate(restTemplateBuilder);
        assertNotNull(restTemplate);
    }

    @Test
    @DisplayName("RestTemplateBuilder 객체가 정상적으로 설정되는지 테스트")
    void restTemplateBuilderIsConfiguredCorrectly() {
        when(restTemplateBuilder.setConnectTimeout(Duration.ofMillis(5000))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.setReadTimeout(Duration.ofMillis(5000))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(mock(RestTemplate.class));

        restTemplateConfig.restTemplate(restTemplateBuilder);

        verify(restTemplateBuilder).setConnectTimeout(Duration.ofMillis(5000));
        verify(restTemplateBuilder).setReadTimeout(Duration.ofMillis(5000));
    }
}