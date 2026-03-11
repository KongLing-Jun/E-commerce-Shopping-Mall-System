package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotNull;

public class OrderCreateRequest {
    @NotNull(message = "AddressId is required")
    private Long addressId;

    // 功能：获取地址id
    public Long getAddressId() {
        return addressId;
    }

    // 功能：设置地址id
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}
