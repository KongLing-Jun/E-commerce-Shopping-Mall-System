package com.thinking.backendmall.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderView {
    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime finishedAt;
    private String expressNo;
    private String expressCompany;
    private List<OrderItemView> items;

    // 功能：获取订单no
    public String getOrderNo() {
        return orderNo;
    }

    // 功能：设置订单no
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    // 功能：获取paidat
    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    // 功能：设置paidat
    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    // 功能：获取shippedat
    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    // 功能：设置shippedat
    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    // 功能：获取finishedat
    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    // 功能：设置finishedat
    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    // 功能：获取物流no
    public String getExpressNo() {
        return expressNo;
    }

    // 功能：设置物流no
    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    // 功能：获取物流公司
    public String getExpressCompany() {
        return expressCompany;
    }

    // 功能：设置物流公司
    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
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
