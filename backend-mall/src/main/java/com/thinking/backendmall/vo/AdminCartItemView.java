package com.thinking.backendmall.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminCartItemView {
    private Long cartItemId;
    private Long userId;
    private String username;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private Integer checked;
    private LocalDateTime createdAt;

    // 功能：获取购物车明细id
    public Long getCartItemId() {
        return cartItemId;
    }

    // 功能：设置购物车明细id
    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
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

    // 功能：获取价格
    public BigDecimal getPrice() {
        return price;
    }

    // 功能：设置价格
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // 功能：获取quantity
    public Integer getQuantity() {
        return quantity;
    }

    // 功能：设置quantity
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // 功能：获取checked
    public Integer getChecked() {
        return checked;
    }

    // 功能：设置checked
    public void setChecked(Integer checked) {
        this.checked = checked;
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
