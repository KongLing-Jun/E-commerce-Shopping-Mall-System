package com.thinking.backendmall.dto;

public class AdminResetPasswordRequest {
    private String newPassword;

    // 功能：获取new密码
    public String getNewPassword() {
        return newPassword;
    }

    // 功能：设置new密码
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
