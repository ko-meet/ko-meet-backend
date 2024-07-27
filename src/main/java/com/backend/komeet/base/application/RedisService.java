package com.backend.komeet.base.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Redis 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> stringRedisTemplate;

    /**
     * Redis에 문자열 형식의 값을 저장하는 메서드.
     */
    public void saveKeyAndValue(
            String key,
            String value,
            int expireTime
    ) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set(key, value);
        stringRedisTemplate.expire(key, expireTime, MINUTES);
    }

    /**
     * Redis에 저장된 값을 가져오는 메서드.
     */
    public String getValueByKey(
            String refreshToken
    ) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        return ops.get(refreshToken);
    }

    /**
     * Redis에 저장된 값을 삭제하는 메서드.
     */
    public void deleteValueByKey(
            String refreshToken
    ) {
        stringRedisTemplate.delete(refreshToken);
    }
}
