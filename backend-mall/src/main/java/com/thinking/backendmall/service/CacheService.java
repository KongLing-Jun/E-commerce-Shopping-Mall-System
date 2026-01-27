package com.thinking.backendmall.service;

import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;

public interface CacheService {
    <T> T get(String key, Class<T> clazz);

    <T> T get(String key, TypeReference<T> typeReference);

    void set(String key, Object value, Duration ttl);

    void delete(String key);

    void deleteByPrefix(String prefix);
}
