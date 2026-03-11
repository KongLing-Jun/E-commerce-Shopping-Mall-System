package com.thinking.backendmall.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderInvoiceView {
    private String invoiceNo;
    private String orderNo;
    private Long userId;
    private String username;
    private Integer status;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String addressSnapshot;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private List<OrderItemView> items = new ArrayList<>();

    // 功能：获取发票no
    public String getInvoiceNo() {
        return invoiceNo;
    }

    // 功能：设置发票no
    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    // 功能：获取订单no
    public String getOrderNo() {
        return orderNo;
    }

    // 功能：设置订单no
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    // 功能：获取用户id
    public Long getUserId() {
        return userId;
    }

    // 功能：设置用户id
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // 功能：获取用户名
    public String getUsername() {
        return username;
    }

    // 功能：设置用户名
    public void setUsername(String username) {
        this.username = username;
    }

    // 功能：获取状态
    public Integer getStatus() {
        return status;
    }

    // 功能：设置状态
    public void setStatus(Integer status) {
        this.status = status;
    }

    // 功能：获取总计金额
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    // 功能：设置总计金额
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    // 功能：获取支付金额
    public BigDecimal getPayAmount() {
        return payAmount;
    }

    // 功能：设置支付金额
    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    // 功能：获取地址快照
    public String getAddressSnapshot() {
        return addressSnapshot;
    }

    // 功能：设置地址快照
    public void setAddressSnapshot(String addressSnapshot) {
        this.addressSnapshot = addressSnapshot;
    }

    // 功能：获取createdat
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 功能：设置createdat
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 功能：获取paidat
    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    // 功能：设置paidat
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    // 功能：获取明细
    public List<OrderItemView> getItems() {
        return items;
    }

    // 功能：设置明细
    public void setItems(List<OrderItemView> items) {
        this.items = items;
    }
}
