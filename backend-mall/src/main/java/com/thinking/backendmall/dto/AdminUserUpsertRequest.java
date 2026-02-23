package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AdminUserUpsertRequest {
    // User name used for account display and login.
    @NotBlank(message = "Username is required")
    private String username;

    // Phone number is used as a unique identity.
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\d{11}$", message = "Phone must be 11 digits")
    private String phone;

    // Optional during edit; blank means keep old password.
    private String password;

    // Target role id assigned by admin.
    @NotNull(message = "Role is required")
    private Long roleId;

    // 1 enabled, 0 disabled; default is enabled in service.
    private Integer status;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
