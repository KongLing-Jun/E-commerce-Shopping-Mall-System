package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotNull;

public class AdminUserRoleRequest {
    @NotNull(message = "RoleId is required")
    private Long roleId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
