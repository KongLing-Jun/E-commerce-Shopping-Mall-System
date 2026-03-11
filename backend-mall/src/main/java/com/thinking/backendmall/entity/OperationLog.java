package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("operation_log")
public class OperationLog {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long adminId;
    private String adminUsername;
    private String action;
    private String target;
    private String detail;
    private String ip;
    private LocalDateTime createdAt;

    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取adminid
    public Long getAdminId() {
        return adminId;
    }

    // 功能：设置adminid
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    // 功能：获取admin用户名
    public String getAdminUsername() {
        return adminUsername;
    }

    // 功能：设置admin用户名
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    // 功能：获取action
    public String getAction() {
        return action;
    }

    // 功能：设置action
    public void setAction(String action) {
        this.action = action;
    }

    // 功能：获取target
    public String getTarget() {
        return target;
    }

    // 功能：设置target
    public void setTarget(String target) {
        this.target = target;
    }

    // 功能：获取详情
    public String getDetail() {
        return detail;
    }

    // 功能：设置详情
    public void setDetail(String detail) {
        this.detail = detail;
    }

    // 功能：获取ip
    public String getIp() {
        return ip;
    }

    // 功能：设置ip
    public void setIp(String ip) {
        this.ip = ip;
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
