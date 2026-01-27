package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotBlank;

public class UserProfileUpdateRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Phone is required")
    private String phone;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
