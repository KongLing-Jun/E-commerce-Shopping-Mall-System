package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotNull;

public class AdminUserRoleRequest {
    @NotNull(message = "RoleId is required")
    private Long roleId;

    // 功能：获取角色id
    public Long getRoleId() {
        return roleId;
    }

    // 功能：设置角色id
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
