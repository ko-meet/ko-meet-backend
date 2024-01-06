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

    private final static String KEY_TOKEN = "token: ";

    /**
     * 사용자 이메일과 리프레시 토큰을 저장하는 메서드입니다.
     *
     * @param account      사용자 계정
     * @param refreshToken 리프레시 토큰
     */
    @Transactional
    public void saveRefreshToken(String account, String refreshToken) {
        // 이메일을 기반으로 한 식별키를 생성합니다.
        String key = KEY_TOKEN + account;

        // 생성된 식별키와 리프레시 토큰을 저장하며, 토큰의 유효 기간은 1440분(24시간)으로 설정합니다.
        saveKeyAndValue(key, refreshToken, 1440);
    }

    /**
     * Redis에 문자열 형식의 값을 저장하는 메서드.
     *
     * @param key        Redis에 저장할 키
     * @param value      Redis에 저장할 값
     * @param expireTime 키와 값의 만료 시간(분 단위)
     */
    private void saveKeyAndValue(String key, String value, int expireTime) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set(key, value);
        stringRedisTemplate.expire(key, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 로그아웃 시 사용자 리프레시토큰을 삭제하는 메서드입니다.
     *
     * @param account 사용자 계정
     */
    @Transactional
    public void deleteRefreshToken(String account) {
        String key = KEY_TOKEN + account;
        stringRedisTemplate.delete(key);
    }
}
