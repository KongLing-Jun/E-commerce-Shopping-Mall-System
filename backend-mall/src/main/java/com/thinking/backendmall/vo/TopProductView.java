package com.thinking.backendmall.vo;

import java.math.BigDecimal;

public class TopProductView {
    private Long productId;
    private String productName;
    private Long totalQuantity;
    private BigDecimal totalAmount;

    // 功能：获取商品id
    public Long getProductId() {
        return productId;
    }

    // 功能：设置商品id
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 功能：获取商品name
    public String getProductName() {
        return productName;
    }

    // 功能：设置商品name
    public void setProductName(String productName) {
        this.productName = productName;
    }

    // 功能：获取总计quantity
    public Long getTotalQuantity() {
        return totalQuantity;
    }

    // 功能：设置总计quantity
    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    // 功能：获取总计金额
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    // 功能：设置总计金额
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
