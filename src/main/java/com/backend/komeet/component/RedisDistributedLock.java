package com.backend.komeet.component;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class RedisDistributedLock {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean acquireLock(String lockKey,
                               String requestId,
                               long expireTimeInSeconds) {
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(
                lockKey,
                requestId,
                expireTimeInSeconds,
                TimeUnit.SECONDS
        );
        return lockAcquired != null && lockAcquired;
    }

    public void releaseLock(String lockKey, String requestId) {
        String storedRequestId = redisTemplate.opsForValue().get(lockKey);
        if (requestId.equals(storedRequestId)) {
            redisTemplate.delete(lockKey);
        }
    }
}
