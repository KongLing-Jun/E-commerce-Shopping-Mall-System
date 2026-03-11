package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminRoleRequest {
    @NotBlank(message = "Role key is required")
    private String roleKey;

    @NotBlank(message = "Role name is required")
    private String roleName;

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
}
