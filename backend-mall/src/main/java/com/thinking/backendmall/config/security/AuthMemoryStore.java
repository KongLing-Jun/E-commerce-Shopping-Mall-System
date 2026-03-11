package com.thinking.backendmall.config.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthMemoryStore {
    private static class AttemptEntry {
        private int count;
        private long expiresAt;

        private AttemptEntry(int count, long expiresAt) {
            this.count = count;
            this.expiresAt = expiresAt;
        }
    }

    private final ConcurrentHashMap<String, AttemptEntry> loginAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    // 功能：判断账号是否处于登录锁定状态。
    public boolean isLocked(String username, int maxAttempts) {
        if (username == null || username.isBlank()) {
            return false;
        }
        String key = username.trim().toLowerCase();
        AttemptEntry entry = loginAttempts.get(key);
        if (entry == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (entry.expiresAt <= now) {
            loginAttempts.remove(key);
            return false;
        }
        return entry.count >= maxAttempts;
    }

    // 功能：记录一次登录失败并刷新锁定窗口。
    public void recordFailure(String username, long lockSeconds) {
        if (username == null || username.isBlank()) {
            return;
        }
        String key = username.trim().toLowerCase();
        long now = System.currentTimeMillis();
        loginAttempts.compute(key, (k, entry) -> {
            long expiresAt = now + Math.max(lockSeconds, 1) * 1000;
            if (entry == null || entry.expiresAt <= now) {
                return new AttemptEntry(1, expiresAt);
            }
            entry.count += 1;
            return entry;
        });
    }

    // 功能：清理账号登录失败记录。
    public void clearFailure(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        loginAttempts.remove(username.trim().toLowerCase());
    }

    // 功能：将已登出的令牌加入黑名单。
    public void blacklistToken(String token, long ttlMillis) {
        if (token == null || token.isBlank() || ttlMillis <= 0) {
            return;
        }
        long expiresAt = System.currentTimeMillis() + ttlMillis;
        tokenBlacklist.put(token, expiresAt);
    }

    // 功能：判断令牌是否在黑名单中。
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        Long expiresAt = tokenBlacklist.get(token);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt <= System.currentTimeMillis()) {
            tokenBlacklist.remove(token);
            return false;
        }
        return true;
    }
}
