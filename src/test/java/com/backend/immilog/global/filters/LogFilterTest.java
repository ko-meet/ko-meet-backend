package com.backend.immilog.global.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.*;

@DisplayName("LogFilter 클래스")
@ExtendWith(MockitoExtension.class)
class LogFilterTest {

    private final LogFilter logFilter = new LogFilter();

    @Test
    @DisplayName("요청 응답 로깅")
    void testDoFilter_logsRequestAndResponse() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/v1/test");
        request.addHeader("Authorization", "Bearer token");
        request.setContent("request body".getBytes());

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // when
        logFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    @DisplayName("멀티파트 요청은 로깅하지 않음")
    void testDoFilter_skipsMultipartRequests() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        request.setRequestURI("/api/v1/upload");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // when
        logFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(any(), any());
    }
}
