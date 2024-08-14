package com.backend.immilog.global.configuration;

import com.backend.immilog.global.exception.AsyncUncaughtExceptionHandlerCustom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableAsync
@DisplayName("AsyncConfig 클래스")
class AsyncConfigTest {

    @Autowired
    private AsyncConfigurer asyncConfigurer;

    private AsyncTaskExecutor executor;

    @BeforeEach
    void setUp() {
        executor = (AsyncTaskExecutor) asyncConfigurer.getAsyncExecutor();
    }

    @Test
    @DisplayName("AsyncExecutor 설정")
    void asyncExecutorIsConfiguredCorrectly() {
        assertThat(executor).isInstanceOf(ThreadPoolTaskExecutor.class);

        ThreadPoolTaskExecutor threadPoolExecutor = (ThreadPoolTaskExecutor) executor;
        assertThat(threadPoolExecutor.getCorePoolSize()).isEqualTo(2);
        assertThat(threadPoolExecutor.getMaxPoolSize()).isEqualTo(10);
        assertThat(threadPoolExecutor.getThreadNamePrefix()).isEqualTo("async-");
    }

    @Test
    @DisplayName("비동기 실행")
    void asyncExecutionWorks() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello, Async", executor);

        assertThat(future.get()).isEqualTo("Hello, Async");
    }

    @Test
    @DisplayName("AsyncUncaughtExceptionHandler 설정")
    void asyncUncaughtExceptionHandlerIsConfigured() {
        AsyncUncaughtExceptionHandler exceptionHandler = asyncConfigurer.getAsyncUncaughtExceptionHandler();
        assertThat(exceptionHandler).isInstanceOf(AsyncUncaughtExceptionHandlerCustom.class);
    }
}
