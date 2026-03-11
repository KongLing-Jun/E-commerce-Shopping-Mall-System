package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    // 功能：获取用户名
    public String getUsername() {
        return username;
    }

    // 功能：设置用户名
    public void setUsername(String username) {
        this.username = username;
    }

    // 功能：获取手机号
    public String getPhone() {
        return phone;
    }

    // 功能：设置手机号
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // 功能：获取密码
    public String getPassword() {
        return password;
    }

    // 功能：设置密码
    public void setPassword(String password) {
        this.password = password;
    }

    // 功能：获取确认密码
    public String getConfirmPassword() {
        return confirmPassword;
    }

    // 功能：设置确认密码
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
