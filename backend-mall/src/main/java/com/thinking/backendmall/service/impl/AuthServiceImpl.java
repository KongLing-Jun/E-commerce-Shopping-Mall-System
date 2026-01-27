package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.config.JwtUtil;
import com.thinking.backendmall.entity.Role;
import com.thinking.backendmall.entity.User;
import com.thinking.backendmall.repository.RoleRepository;
import com.thinking.backendmall.repository.UserRepository;
import com.thinking.backendmall.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${app.login.max-attempts:5}")
    private int maxAttempts;

    @Value("${app.login.lock-seconds:900}")
    private long lockSeconds;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Map<String, Object> register(String username, String phone, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new BusinessException("Passwords do not match");
        }
        User existingByUsername = userRepository.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (existingByUsername != null) {
            throw new BusinessException("Username already exists");
        }
        User existingByPhone = userRepository.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (existingByPhone != null) {
            throw new BusinessException("Phone already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setStatus(1);
        Role userRole = roleRepository.selectOne(
                new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, "USER"));
        if (userRole != null) {
            user.setRoleId(userRole.getId());
        }
        user.setCreatedAt(LocalDateTime.now());
        userRepository.insert(user);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        return result;
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        if (isLocked(username)) {
            throw new BusinessException("Too many login attempts. Please try later.");
        }
        User user = userRepository.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            recordFailure(username);
            throw new BusinessException("User not found");
        }
        if (user.getStatus() != 1) {
            recordFailure(username);
            throw new BusinessException("User is disabled");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            recordFailure(username);
            throw new BusinessException("Invalid password");
        }
        clearFailure(username);

        Role role = roleRepository.selectById(user.getRoleId());
        String roleKey = role != null ? role.getRoleKey() : "USER";

        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), roleKey);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("roleKey", roleKey);
        return result;
    }

    private boolean isLocked(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        try {
            String value = redisTemplate.opsForValue().get(loginFailKey(username));
            if (value == null) {
                return false;
            }
            int attempts = Integer.parseInt(value);
            return attempts >= maxAttempts;
        } catch (Exception ex) {
            return false;
        }
    }

    private void recordFailure(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        try {
            Long attempts = redisTemplate.opsForValue().increment(loginFailKey(username));
            if (attempts != null && attempts == 1L) {
                redisTemplate.expire(loginFailKey(username), lockSeconds, TimeUnit.SECONDS);
            }
        } catch (Exception ex) {
            // Ignore Redis failures.
        }
    }

    private void clearFailure(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        try {
            redisTemplate.delete(loginFailKey(username));
        } catch (Exception ex) {
            // Ignore Redis failures.
        }
    }

    private String loginFailKey(String username) {
        return "login:fail:" + username.toLowerCase();
    }
}
