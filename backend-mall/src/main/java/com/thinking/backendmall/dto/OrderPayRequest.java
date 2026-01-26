package com.thinking.backendmall.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class OrderPayRequest {
    @DecimalMin(value = "0.01", inclusive = false, message = "PayAmount must be greater than 0")
    private BigDecimal payAmount;

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }
}
