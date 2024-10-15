package com.backend.immilog.global.configuration;

import com.backend.immilog.global.filters.LogFilter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("FilterConfig 클래스 테스트")
class FilterConfigTest {
    private final FilterConfig filterConfig = new FilterConfig();

    @Test
    @DisplayName("FilterRegistrationBean 객체가 정상적으로 생성되는지 테스트")
    void loggingFilterIsRegistered() {
        FilterRegistrationBean<LogFilter> registrationBean = filterConfig.loggingFilter();
        assertNotNull(registrationBean);
    }

    @Test
    @DisplayName("FilterRegistrationBean 객체가 LogFilter 클래스의 인스턴스인지 테스트")
    void loggingFilterHasCorrectUrlPatterns() {
        FilterRegistrationBean<LogFilter> registrationBean = filterConfig.loggingFilter();
        assertEquals("/*", registrationBean.getUrlPatterns().iterator().next());
    }

    @Test
    @DisplayName("FilterRegistrationBean 객체가 LogFilter 클래스의 인스턴스인지 테스트")
    void loggingFilterHasCorrectOrder() {
        FilterRegistrationBean<LogFilter> registrationBean = filterConfig.loggingFilter();
        assertEquals(1, registrationBean.getOrder());
    }
}