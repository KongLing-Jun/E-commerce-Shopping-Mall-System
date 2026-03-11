package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;
    private String phone;
    private String passwordHash;
    private Integer status; // 1正常 0禁用
    private Long roleId;

    private LocalDateTime createdAt;
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

    // 功能：获取密码hash
    public String getPasswordHash() {
        return passwordHash;
    }

    // 功能：设置密码hash
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // 功能：获取状态
    public Integer getStatus() {
        return status;
    }

    // 功能：设置状态
    public void setStatus(Integer status) {
        this.status = status;
    }

    // 功能：获取角色id
    public Long getRoleId() {
        return roleId;
    }

    // 功能：设置角色id
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    // 功能：获取createdat
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 功能：设置createdat
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
