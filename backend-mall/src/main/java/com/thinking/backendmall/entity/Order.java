package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@TableName("`order`")
public class Order {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    // 0: pending payment, 1: paid (awaiting shipment), 2: shipped, 3: completed
    private Integer status;
    private String addressSnapshot;

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime finishedAt;

    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取finishedat
    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    // 功能：设置finishedat
    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    // 功能：获取shippedat
    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    // 功能：设置shippedat
    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    // 功能：获取paidat
    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    // 功能：设置paidat
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    // 功能：获取createdat
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 功能：设置createdat
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 功能：获取地址快照
    public String getAddressSnapshot() {
        return addressSnapshot;
    }

    // 功能：设置地址快照
    public void setAddressSnapshot(String addressSnapshot) {
        this.addressSnapshot = addressSnapshot;
    }

    // 功能：获取状态
    public Integer getStatus() {
        return status;
    }

    // 功能：设置状态
    public void setStatus(Integer status) {
        this.status = status;
    }

    // 功能：获取支付金额
    public BigDecimal getPayAmount() {
        return payAmount;
    }

    // 功能：设置支付金额
    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    // 功能：获取总计金额
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    // 功能：设置总计金额
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    // 功能：获取用户id
    public Long getUserId() {
        return userId;
    }

    // 功能：设置用户id
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // 功能：获取订单no
    public String getOrderNo() {
        return orderNo;
    }

    // 功能：设置订单no
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}