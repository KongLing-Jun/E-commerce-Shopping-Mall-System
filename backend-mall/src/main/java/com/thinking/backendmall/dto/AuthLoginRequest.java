package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthLoginRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    // 功能：获取用户名
    public String getUsername() {
        return username;
    }

    // 功能：设置用户名
    public void setUsername(String username) {
        this.username = username;
    }

    // 功能：获取密码
    public String getPassword() {
        return password;
    }

    // 功能：设置密码
    public void setPassword(String password) {
        this.password = password;
    }
}
