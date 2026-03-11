package com.thinking.backendmall.vo;

import java.time.LocalDateTime;

public class UserProfileView {
    private Long id;
    private String username;
    private String phone;
    private String roleKey;
    private String roleName;
    private LocalDateTime createdAt;
    private String token;

    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

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

    // 功能：获取角色key
    public String getRoleKey() {
        return roleKey;
    }

    // 功能：设置角色key
    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    // 功能：获取角色name
    public String getRoleName() {
        return roleName;
    }

    // 功能：设置角色name
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    // 功能：获取createdat
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 功能：设置createdat
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 功能：获取令牌
    public String getToken() {
        return token;
    }

    // 功能：设置令牌
    public void setToken(String token) {
        this.token = token;
    }
}
