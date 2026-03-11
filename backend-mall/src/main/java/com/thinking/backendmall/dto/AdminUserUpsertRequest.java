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

    // 功能：获取角色id
    public Long getRoleId() {
        return roleId;
    }

    // 功能：设置角色id
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    // 功能：获取状态
    public Integer getStatus() {
        return status;
    }

    // 功能：设置状态
    public void setStatus(Integer status) {
        this.status = status;
    }
}
