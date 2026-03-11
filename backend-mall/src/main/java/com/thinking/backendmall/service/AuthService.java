package com.thinking.backendmall.service;

import java.util.Map;

public interface AuthService {
    // 功能：注册数据
    Map<String, Object> register(String username, String phone, String password, String confirmPassword);

    // 功能：登录数据
    Map<String, Object> login(String username, String password);

    // 功能：退出登录数据
    void logout(String token);
}
