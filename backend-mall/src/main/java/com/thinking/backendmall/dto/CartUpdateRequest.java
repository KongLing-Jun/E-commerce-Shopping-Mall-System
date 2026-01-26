package com.thinking.backendmall.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class CartUpdateRequest {
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Min(value = 0, message = "Checked must be 0 or 1")
    @Max(value = 1, message = "Checked must be 0 or 1")
    private Integer checked;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }
}
