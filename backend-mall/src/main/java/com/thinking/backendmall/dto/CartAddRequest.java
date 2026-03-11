package com.thinking.backendmall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartAddRequest {
    @NotNull(message = "ProductId is required")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    // 功能：获取商品id
    public Long getProductId() {
        return productId;
    }

    // 功能：设置商品id
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 功能：获取quantity
    public Integer getQuantity() {
        return quantity;
    }

    // 功能：设置quantity
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
