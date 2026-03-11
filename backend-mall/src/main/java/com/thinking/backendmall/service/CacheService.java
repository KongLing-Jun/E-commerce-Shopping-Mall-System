package com.thinking.backendmall.service;

import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;

public interface CacheService {
    // 功能：获取数据
    <T> T get(String key, Class<T> clazz);

    // 功能：获取数据
    <T> T get(String key, TypeReference<T> typeReference);

    // 功能：处理set
    void set(String key, Object value, Duration ttl);

    // 功能：删除数据
    void delete(String key);

    // 功能：删除byprefix
    void deleteByPrefix(String prefix);
}
