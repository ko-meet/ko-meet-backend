package com.backend.immilog.global.components;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("분산락 테스트")
class RedisDistributedLockTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisDistributedLock redisDistributedLock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisDistributedLock = new RedisDistributedLock(redisTemplate);
    }

    @Test
    @DisplayName("lock 획득 성공")
    void acquireLock_success() {
        // given
        String lockKey = "testLock";
        String requestId = "1234";
        long expireTime = 10L;

        when(valueOperations.setIfAbsent(
                lockKey,
                requestId,
                expireTime,
                TimeUnit.SECONDS
        )).thenReturn(true);

        // when
        boolean result = redisDistributedLock.acquireLock(
                lockKey,
                requestId,
                expireTime
        );

        // then
        assertThat(result).isTrue();
        verify(valueOperations, times(1))
                .setIfAbsent(
                        lockKey,
                        requestId,
                        expireTime,
                        TimeUnit.SECONDS
                );
    }

    @Test
    @DisplayName("lock 해제 성공")
    void releaseLock_success() {
        // given
        String lockKey = "testLock";
        String requestId = "1234";

        when(valueOperations.get(lockKey)).thenReturn(requestId);

        // when
        redisDistributedLock.releaseLock(lockKey, requestId);

        // then
        verify(redisTemplate, times(1)).delete(lockKey);
    }

    @Test
    @DisplayName("lock 해제 실패: requestId가 일치하지 않음")
    void releaseLock_noDeletionIfRequestIdDoesNotMatch() {
        // given
        String lockKey = "testLock";
        String requestId = "1234";

        when(valueOperations.get(lockKey)).thenReturn("anotherRequestId");

        // when
        redisDistributedLock.releaseLock(lockKey, requestId);

        // then
        verify(redisTemplate, never()).delete(lockKey);
    }
}
