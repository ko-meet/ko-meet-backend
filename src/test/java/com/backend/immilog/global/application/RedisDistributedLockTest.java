package com.backend.immilog.global.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RedisDistributedLockTest {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    private RedisDistributedLock redisDistributedLock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redisDistributedLock = new RedisDistributedLock(redisTemplate);
    }

    @Test
    @DisplayName("락 획득 시도 (최대 3회)")
    void acquireLock_FailsTwiceThenSucceeds() {
        // given
        String lockKey = "lockKey";
        String requestId = "requestId";
        ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);

        // 첫 번째와 두 번째 시도는 실패 (false 반환)
        when(valueOperations.setIfAbsent(
                lockKey, requestId, 10, TimeUnit.SECONDS
        ))
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(true);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        boolean result = redisDistributedLock.tryAcquireLock(lockKey, requestId);

        // then
        assertThat(result).isTrue();

        // 세 번 시도했는지 확인
        verify(valueOperations, times(3)).setIfAbsent(lockKey, requestId, 10, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("락 해제 성공")
    void releaseLock_WhenRequestIdMatches_ShouldDeleteLock() {
        // given
        String lockKey = "lockKey";
        String requestId = "requestId";
        ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(lockKey)).thenReturn(requestId);

        // when
        redisDistributedLock.releaseLock(lockKey, requestId);

        // then
        verify(redisTemplate, times(1)).delete(lockKey);
    }

    @Test
    @DisplayName("락 해제 실패 : 이미 없는 키")
    void releaseLock_WhenRequestIdDoesNotMatch_ShouldNotDeleteLock() {
        // given
        String lockKey = "lockKey";
        String requestId = "requestId";
        String differentRequestId = "differentRequestId";
        ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(lockKey)).thenReturn(differentRequestId);

        // when
        redisDistributedLock.releaseLock(lockKey, requestId);

        // then
        verify(redisTemplate, never()).delete(lockKey);
    }

    @Test
    @DisplayName("락 해제 실패 : 예외 발생")
    void releaseLock_WhenExceptionThrown_ShouldLogError() {
        // given
        String lockKey = "lockKey";
        String requestId = "requestId";
        ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(lockKey)).thenThrow(new DataAccessException(
                "Error") {});

        // when
        redisDistributedLock.releaseLock(lockKey, requestId);

        // then
        verify(redisTemplate, never()).delete(lockKey);
    }
}