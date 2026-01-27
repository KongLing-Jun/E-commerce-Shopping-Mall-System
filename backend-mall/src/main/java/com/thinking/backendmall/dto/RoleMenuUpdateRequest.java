package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class RoleMenuUpdateRequest {
    @NotNull(message = "MenuIds is required")
    private List<Long> menuIds;

    public List<Long> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<Long> menuIds) {
        this.menuIds = menuIds;
    }
}
