package com.backend.immilog.global.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.MINUTES;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> stringRedisTemplate;

    public void saveKeyAndValue(
            String key,
            String value,
            int expireTime
    ) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set(key, value);
        stringRedisTemplate.expire(key, expireTime, MINUTES);
    }

    public String getValueByKey(
            String refreshToken
    ) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        return ops.get(refreshToken);
    }

    public void deleteValueByKey(
            String refreshToken
    ) {
        stringRedisTemplate.delete(refreshToken);
    }
}

