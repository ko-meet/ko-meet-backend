package com.backend.immilog.global.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDistributedLock {
    private final RedisTemplate<String, String> redisTemplate;
    final int MAX_RETRY = 3;
    final int EXPIRE_TIME_IN_SECONDS = 10;
    final long RETRY_DELAY_MILLIS = 500;

    public boolean tryAcquireLock(
            String key,
            String targetSeq
    ) {
        boolean lockAcquired = false;
        int retryCount = 0;
        while (!lockAcquired && retryCount < MAX_RETRY) {
            lockAcquired = acquireLock(
                    key,
                    targetSeq,
                    EXPIRE_TIME_IN_SECONDS
            );
            retryCount = getRetryCount(lockAcquired, retryCount);
        }
        return lockAcquired;
    }

    public void releaseLock(
            String lockKey,
            String requestId
    ) {
        try {
            String storedRequestId = redisTemplate.opsForValue().get(lockKey);
            if (requestId.equals(storedRequestId)) {
                redisTemplate.delete(lockKey);
            }
        } catch (DataAccessException e) {
            log.error("Failed to release lock", e);
        }
    }

    private boolean acquireLock(
            String lockKey,
            String requestId,
            long expireTimeInSeconds
    ) {
        try {
            Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(
                    lockKey,
                    requestId,
                    expireTimeInSeconds,
                    TimeUnit.SECONDS
            );
            return lockAcquired != null && lockAcquired;
        } catch (DataAccessException e) {
            log.error("Failed to acquire lock", e);
            return false;
        }
    }

    public int getRetryCount(
            boolean lockAcquired,
            int retryCount
    ) {
        if (!lockAcquired) {
            try {
                Thread.sleep(RETRY_DELAY_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            retryCount++;
        }
        return retryCount;
    }
}

