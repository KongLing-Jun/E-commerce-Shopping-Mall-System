package com.thinking.backendmall.vo;

import com.thinking.backendmall.entity.Address;

import java.math.BigDecimal;
import java.util.List;

public class OrderPreResponse {
    private List<CartItemView> items;
    private List<Address> addresses;
    private BigDecimal totalAmount;

    public List<CartItemView> getItems() {
        return items;
    }

    public void setItems(List<CartItemView> items) {
        this.items = items;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
