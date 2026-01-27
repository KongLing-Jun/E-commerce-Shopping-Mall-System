package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotNull;

public class UserFavoriteRequest {
    @NotNull(message = "ProductId is required")
    private Long productId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
