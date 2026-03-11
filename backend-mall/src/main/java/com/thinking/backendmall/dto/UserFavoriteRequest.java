package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotNull;

public class UserFavoriteRequest {
    @NotNull(message = "ProductId is required")
    private Long productId;

    // 功能：获取商品id
    public Long getProductId() {
        return productId;
    }

    // 功能：设置商品id
    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
