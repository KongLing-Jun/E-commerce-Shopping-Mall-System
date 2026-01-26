package com.thinking.backendmall.service;

import java.util.Map;

public interface AuthService {
    Map<String, Object> register(String username, String phone, String password, String confirmPassword);

    Map<String, Object> login(String username, String password);
}
