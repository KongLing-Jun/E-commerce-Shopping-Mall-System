package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class RoleMenuUpdateRequest {
    @NotNull(message = "MenuIds is required")
    private List<Long> menuIds;

    // 功能：获取菜单ids
    public List<Long> getMenuIds() {
        return menuIds;
    }

    // 功能：设置菜单ids
    public void setMenuIds(List<Long> menuIds) {
        this.menuIds = menuIds;
    }
}
