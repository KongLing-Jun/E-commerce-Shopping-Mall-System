package com.thinking.backendmall.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class OrderPayRequest {
    @DecimalMin(value = "0.01", inclusive = false, message = "PayAmount must be greater than 0")
    private BigDecimal payAmount;

    // 功能：获取支付金额
    public BigDecimal getPayAmount() {
        return payAmount;
    }

    // 功能：设置支付金额
    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }
}
