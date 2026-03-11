package com.thinking.backendmall.vo;

import java.time.LocalDateTime;

public class OrderTrackingEventView {
    private String title;
    private String description;
    private String location;
    private Integer status;
    private LocalDateTime eventTime;

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

    // 功能：获取关联状态。
    public Integer getStatus() {
        return status;
    }

    // 功能：设置关联状态。
    public void setStatus(Integer status) {
        this.status = status;
    }

    // 功能：获取轨迹时间。
    public LocalDateTime getEventTime() {
        return eventTime;
    }

    // 功能：设置轨迹时间。
    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }
}
