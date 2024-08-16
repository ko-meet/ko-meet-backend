package com.backend.immilog.global.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Redis 키/값 테스트")
class RedisServiceTest {
    @Mock
    private RedisTemplate<String, String> stringRedisTemplate;
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redisService = new RedisService(stringRedisTemplate);
    }

    @Test
    @DisplayName("키/값 저장")
    void saveKeyAndValue() {
        // given
        String key = "key";
        String value = "value";
        int expireTime = 10;
        ValueOperations<String, String> ops =
                mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue())
                .thenReturn(ops);
        // when
        redisService.saveKeyAndValue(key, value, expireTime);
        // then
        verify(ops).set(key, value);
    }

    @Test
    @DisplayName("키로 값 가져오기")
    void getValueByKey() {
        // given
        String key = "key";
        String value = "value";
        ValueOperations<String, String> ops =
                mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue())
                .thenReturn(ops);
        when(ops.get(key))
                .thenReturn(value);
        // when
        String result = redisService.getValueByKey(key);
        // then
        assertThat(result).isEqualTo(value);
    }

    @Test
    @DisplayName("키로 값 삭제")
    void deleteValueByKey() {
        // given
        String key = "key";
        // when
        redisService.deleteValueByKey(key);
        // then
        verify(stringRedisTemplate).delete(key);
    }
}