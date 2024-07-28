package com.backend.komeet.global.components;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 분산 락
 */
@RequiredArgsConstructor
@Component
public class RedisDistributedLock {
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 락 획득
     */
    public boolean acquireLock(
            String lockKey,
            String requestId,
            long expireTimeInSeconds
    ) {
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(
                lockKey,
                requestId,
                expireTimeInSeconds,
                TimeUnit.SECONDS
        );
        return lockAcquired != null && lockAcquired;
    }

    /**
     * 락 해제
     */
    public void releaseLock(
            String lockKey,
            String requestId
    ) {
        String storedRequestId = redisTemplate.opsForValue().get(lockKey);
        if (requestId.equals(storedRequestId)) {
            redisTemplate.delete(lockKey);
        }
    }
}
