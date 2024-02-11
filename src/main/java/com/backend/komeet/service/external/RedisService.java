package com.backend.komeet.service.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

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
     *
     * @param key        Redis에 저장할 키
     * @param value      Redis에 저장할 값
     * @param expireTime 키와 값의 만료 시간(분 단위)
     */
    public void saveKeyAndValue(String key, String value, int expireTime) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set(key, value);
        stringRedisTemplate.expire(key, expireTime, TimeUnit.MINUTES);
    }

    /**
     * Redis에 저장된 값을 가져오는 메서드.
     *
     * @param refreshToken Redis에 저장된 키
     * @return Redis에 저장된 값
     */
    public String getValueByKey(String refreshToken) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        return ops.get(refreshToken);
    }

    public void deleteValueByKey(String refreshToken) {
        stringRedisTemplate.delete(refreshToken);
    }
}
