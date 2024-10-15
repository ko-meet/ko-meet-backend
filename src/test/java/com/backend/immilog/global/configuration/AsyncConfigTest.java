package com.backend.immilog.global.configuration;

import com.backend.immilog.global.exception.AsyncUncaughtExceptionHandlerCustom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@DisplayName("AsyncConfig 클래스 테스트")
class AsyncConfigTest {

    private final AsyncConfig asyncConfig = new AsyncConfig();

    @Test
    @DisplayName("AsyncConfig 객체가 정상적으로 생성되는지 테스트")
    void asyncExecutorIsNotNull() {
        Executor executor = asyncConfig.getAsyncExecutor();
        assertNotNull(executor);
    }

    @Test
    @DisplayName("예외 핸들러가 null이 아닌지 테스트")
    void asyncUncaughtExceptionHandlerIsNotNull() {
        AsyncUncaughtExceptionHandler exceptionHandler = asyncConfig.getAsyncUncaughtExceptionHandler();
        assertNotNull(exceptionHandler);
    }

    @Test
    @DisplayName("예외 핸들러가 AsyncUncaughtExceptionHandlerCustom 클래스의 인스턴스인지 테스트")
    void asyncUncaughtExceptionHandlerIsCustomHandler() {
        AsyncUncaughtExceptionHandler exceptionHandler = asyncConfig.getAsyncUncaughtExceptionHandler();
        assertNotNull(exceptionHandler);
        assert(exceptionHandler instanceof AsyncUncaughtExceptionHandlerCustom);
    }
}