package com.thinking.backendmall.vo;

import java.time.LocalDateTime;

public class MerchantNoticeView {
    private Long id;
    private String noticeType;
    private String orderNo;
    private String content;
    private Integer status;
    private LocalDateTime createdAt;

    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取通知type
    public String getNoticeType() {
        return noticeType;
    }

    // 功能：设置通知type
    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    // 功能：获取订单no
    public String getOrderNo() {
        return orderNo;
    }

    // 功能：设置订单no
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    // 功能：获取content
    public String getContent() {
        return content;
    }

    // 功能：设置content
    public void setContent(String content) {
        this.content = content;
    }

    // 功能：获取状态
    public Integer getStatus() {
        return status;
    }

    // 功能：设置状态
    public void setStatus(Integer status) {
        this.status = status;
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
