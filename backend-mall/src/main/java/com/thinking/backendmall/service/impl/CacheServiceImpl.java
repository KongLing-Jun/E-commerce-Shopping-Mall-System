package com.thinking.backendmall.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinking.backendmall.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value, clazz);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public <T> T get(String key, TypeReference<T> typeReference) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value, typeReference);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void set(String key, Object value, Duration ttl) {
        if (value == null) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(value);
            if (ttl == null || ttl.isZero() || ttl.isNegative()) {
                redisTemplate.opsForValue().set(key, json);
            } else {
                redisTemplate.opsForValue().set(key, json, ttl);
            }
        } catch (Exception ex) {
            // Ignore cache write failures.
        }
    }

    @Override
    public void delete(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        try {
            redisTemplate.delete(key);
        } catch (Exception ex) {
            // Ignore cache delete failures.
        }
    }

    @Override
    public void deleteByPrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return;
        }
        try {
            Set<String> keys = redisTemplate.keys(prefix + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception ex) {
            // Ignore cache delete failures.
        }
    }
}
