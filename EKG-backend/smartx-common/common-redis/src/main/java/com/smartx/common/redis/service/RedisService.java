package com.smartx.common.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisService {

    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    // 存入缓存并设置过期时间
    public void setCacheObject(final String key, final Object value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    // 获取缓存
    public Object getCacheObject(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 删除缓存
    public boolean deleteObject(final String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}
