package com.wemap.api.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void setValueWithTimeout(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    @Transactional(readOnly = true)
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Transactional(readOnly = true)
    public Long getExpireToMilliseconds(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }

    @Transactional
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
}
