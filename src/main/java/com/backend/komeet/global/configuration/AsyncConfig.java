package com.backend.komeet.global.configuration;

import com.backend.komeet.global.exception.AsyncUncaughtExceptionHandlerCustom;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 작업을 위한 Spring Async 구성
 */
@Configuration
@EnableAsync
public class AsyncConfig extends AsyncConfigurerSupport {

    /**
     * 비동기 작업에 사용될 Executor 설정
     */
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // 디폴트로 실행 대기 중인 Thread 수
        executor.setMaxPoolSize(10); // 동시에 동작하는 최대 Thread 수
        executor.setQueueCapacity(20); // CorePool이 초과될때 Queue에 저장했다가 꺼내서 실행. (20개까지 저장)
        executor.setThreadNamePrefix("async-"); // Spring에서 생성하는 Thread 접두사
        executor.initialize();
        return executor;
    }

    /**
     * 비동기 작업 중 예외 처리를 담당하는 핸들러 반환
     */
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandlerCustom();
    }
}