package org.disk.service.impl;

import lombok.AllArgsConstructor;
import org.disk.service.IRedisService;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class RedisServiceImpl implements IRedisService {
    private final RedisTemplate redisTemplate;


    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }


    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public <T> T getCacheValue(final String key) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        return (T) valueOperations.get(key);
    }

}
