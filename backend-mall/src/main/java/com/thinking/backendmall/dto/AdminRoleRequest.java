package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminRoleRequest {
    @NotBlank(message = "Role key is required")
    private String roleKey;

    @NotBlank(message = "Role name is required")
    private String roleName;

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
