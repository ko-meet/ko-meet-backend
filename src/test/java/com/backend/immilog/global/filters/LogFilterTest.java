package com.backend.immilog.global.filters;

import com.backend.immilog.global.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.Mockito.*;

@DisplayName("LogFilter 클래스")
@SpringBootTest
class LogFilterTest {

    private LogFilter logFilter;

    @MockBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        logFilter = new LogFilter();
    }

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
