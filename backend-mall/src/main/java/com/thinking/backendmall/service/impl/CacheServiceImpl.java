package com.thinking.backendmall.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinking.backendmall.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CacheServiceImpl implements CacheService {

    private static class CacheEntry {
        private final String value;
        private final long expiresAt;

        private CacheEntry(String value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, CacheEntry> localCache = new ConcurrentHashMap<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    // 功能：获取缓存数据（内存实现）。
    public <T> T get(String key, Class<T> clazz) {
        try {
            CacheEntry entry = localCache.get(key);
            if (entry == null) {
                return null;
            }
            if (isExpired(entry)) {
                localCache.remove(key);
                return null;
            }
            return objectMapper.readValue(entry.value, clazz);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    // 功能：获取缓存数据（内存实现）。
    public <T> T get(String key, TypeReference<T> typeReference) {
        try {
            CacheEntry entry = localCache.get(key);
            if (entry == null) {
                return null;
            }
            if (isExpired(entry)) {
                localCache.remove(key);
                return null;
            }
            return objectMapper.readValue(entry.value, typeReference);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    // 功能：写入缓存数据（内存实现）。
    public void set(String key, Object value, Duration ttl) {
        if (value == null || key == null || key.isBlank()) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(value);
            long expiresAt = ttl == null || ttl.isZero() || ttl.isNegative()
                    ? Long.MAX_VALUE
                    : System.currentTimeMillis() + ttl.toMillis();
            localCache.put(key, new CacheEntry(json, expiresAt));
        } catch (Exception ex) {
            // Ignore cache write failures.
        }
    }

    @Override
    // 功能：删除指定缓存数据。
    public void delete(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        localCache.remove(key);
    }

    @Override
    // 功能：按前缀批量删除缓存数据。
    public void deleteByPrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return;
        }
        Set<String> keys = localCache.keySet().stream()
                .filter(key -> key.startsWith(prefix))
                .collect(Collectors.toSet());
        keys.forEach(localCache::remove);
    }

    // 功能：判断缓存是否过期。
    private boolean isExpired(CacheEntry entry) {
        return entry.expiresAt != Long.MAX_VALUE && entry.expiresAt <= System.currentTimeMillis();
    }
}
