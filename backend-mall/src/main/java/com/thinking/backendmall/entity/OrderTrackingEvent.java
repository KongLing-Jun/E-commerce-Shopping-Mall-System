package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("order_tracking_event")
public class OrderTrackingEvent {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Integer status;
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventTime;
    private LocalDateTime createdAt;

    // 功能：获取事件ID。
    public Long getId() {
        return id;
    }

    // 功能：设置事件ID。
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取所属订单ID。
    public Long getOrderId() {
        return orderId;
    }

    // 功能：设置所属订单ID。
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    // 功能：获取订单状态快照。
    public Integer getStatus() {
        return status;
    }

    // 功能：设置订单状态快照。
    public void setStatus(Integer status) {
        this.status = status;
    }

    // 功能：获取轨迹标题。
    public String getTitle() {
        return title;
    }

    // 功能：设置轨迹标题。
    public void setTitle(String title) {
        this.title = title;
    }

    // 功能：获取轨迹描述。
    public String getDescription() {
        return description;
    }

    // 功能：设置轨迹描述。
    public void setDescription(String description) {
        this.description = description;
    }

    // 功能：获取轨迹地点。
    public String getLocation() {
        return location;
    }

    // 功能：设置轨迹地点。
    public void setLocation(String location) {
        this.location = location;
    }

    // 功能：获取轨迹发生时间。
    public LocalDateTime getEventTime() {
        return eventTime;
    }

    // 功能：设置轨迹发生时间。
    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    // 功能：获取轨迹记录创建时间。
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 功能：设置轨迹记录创建时间。
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
