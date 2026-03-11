package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("role")
public class Role {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String roleKey; // ADMIN/USER
    private String roleName;

    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
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
}
