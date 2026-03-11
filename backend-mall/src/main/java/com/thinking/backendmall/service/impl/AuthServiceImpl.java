package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.config.JwtUtil;
import com.thinking.backendmall.config.security.AuthMemoryStore;
import com.thinking.backendmall.entity.Role;
import com.thinking.backendmall.entity.User;
import com.thinking.backendmall.repository.RoleRepository;
import com.thinking.backendmall.repository.UserRepository;
import com.thinking.backendmall.service.AuthService;
import com.thinking.backendmall.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MenuService menuService;

    @Autowired
    private AuthMemoryStore authMemoryStore;

    @Value("${app.login.max-attempts:5}")
    private int maxAttempts;

    @Value("${app.login.lock-seconds:900}")
    private long lockSeconds;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    // 功能：初始化认证服务所需的密码加密器。
    public AuthServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    // 功能：注册账号并写入用户信息。
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
    // 功能：校验账号密码并返回登录信息。
    public Map<String, Object> login(String username, String password) {
        if (authMemoryStore.isLocked(username, maxAttempts)) {
            throw new BusinessException("Too many login attempts. Please try later.");
        }
        User user = userRepository.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            authMemoryStore.recordFailure(username, lockSeconds);
            throw new BusinessException("User not found");
        }
        if (user.getStatus() != 1) {
            authMemoryStore.recordFailure(username, lockSeconds);
            throw new BusinessException("User is disabled");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            authMemoryStore.recordFailure(username, lockSeconds);
            throw new BusinessException("Invalid password");
        }
        authMemoryStore.clearFailure(username);

        Role role = roleRepository.selectById(user.getRoleId());
        String roleKey = role != null ? role.getRoleKey() : "USER";

        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), roleKey);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("roleKey", roleKey);
        result.put("menus", menuService.listMyMenus(roleKey));
        result.put("perms", menuService.listMyPerms(roleKey));
        return result;
    }

    @Override
    // 功能：登出时将令牌加入本地黑名单。
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        try {
            var claims = jwtUtil.getClaimsFromToken(token);
            long ttl = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                authMemoryStore.blacklistToken(token, ttl);
            }
        } catch (Exception ex) {
            // Ignore invalid token.
        }
    }
}
