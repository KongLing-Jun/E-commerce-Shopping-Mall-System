package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
@TableName("role_menu")
public class RoleMenu {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long roleId;
    private Long menuId;
    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取角色id
    public Long getRoleId() {
        return roleId;
    }

    // 功能：设置角色id
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    // 功能：获取菜单id
    public Long getMenuId() {
        return menuId;
    }

    // 功能：设置菜单id
    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }
}
