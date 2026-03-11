package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotBlank;

public class UserPasswordChangeRequest {
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    // 功能：获取old密码
    public String getOldPassword() {
        return oldPassword;
    }

    // 功能：设置old密码
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    // 功能：获取new密码
    public String getNewPassword() {
        return newPassword;
    }

    // 功能：设置new密码
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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
