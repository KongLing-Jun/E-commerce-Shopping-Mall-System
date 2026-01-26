package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotNull;

public class OrderCreateRequest {
    @NotNull(message = "AddressId is required")
    private Long addressId;

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}
