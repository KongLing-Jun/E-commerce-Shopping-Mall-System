package com.thinking.backendmall.service;

import com.thinking.backendmall.dto.AuthResponse;
import com.thinking.backendmall.dto.LoginRequest;
import com.thinking.backendmall.dto.RegisterRequest;
import com.thinking.backendmall.model.User;
import com.thinking.backendmall.repository.InMemoryStore;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class AuthService {
    private final InMemoryStore store;
    private final PasswordHasher hasher = new PasswordHasher();

    public AuthService(InMemoryStore store) {
        this.store = store;
    }

    public AuthResponse register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("密码至少6位");
        }
        store.getUsers().values().forEach(user -> {
            if (Objects.equals(user.getUsername(), request.getUsername())) {
                throw new IllegalArgumentException("用户名已存在");
            }
        });
        Long userId = store.nextUserId();
        User user = new User(userId, request.getUsername(), hasher.hash(request.getPassword()), request.getPhone(), true);
        store.getUsers().put(userId, user);
        return issueToken(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = store.getUsers().values().stream()
                .filter(item -> Objects.equals(item.getUsername(), request.getUsername()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("账号不存在"));
        if (!user.isActive()) {
            throw new IllegalStateException("账号已被禁用");
        }
        String hashed = hasher.hash(request.getPassword() == null ? "" : request.getPassword());
        if (!Objects.equals(user.getPasswordHash(), hashed)) {
            throw new IllegalArgumentException("密码错误");
        }
        return issueToken(user);
    }

    public Long resolveUserId(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("请先登录");
        }
        Long userId = store.getTokens().get(token);
        if (userId == null) {
            throw new IllegalArgumentException("登录信息失效");
        }
        return userId;
    }

    private AuthResponse issueToken(User user) {
        String token = UUID.randomUUID().toString();
        store.getTokens().put(token, user.getId());
        return new AuthResponse(token, user.getId(), user.getUsername());
    }
}
