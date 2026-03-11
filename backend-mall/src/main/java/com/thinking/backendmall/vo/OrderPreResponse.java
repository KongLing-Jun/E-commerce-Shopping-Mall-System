package com.thinking.backendmall.vo;

import com.thinking.backendmall.entity.Address;
import java.math.BigDecimal;
import java.util.List;

public class OrderPreResponse {
    private List<CartItemView> items;
    private List<Address> addresses;
    private BigDecimal totalAmount;

    // 功能：获取明细
    public List<CartItemView> getItems() {
        return items;
    }

    // 功能：设置明细
    public void setItems(List<CartItemView> items) {
        this.items = items;
    }

    // 功能：获取地址
    public List<Address> getAddresses() {
        return addresses;
    }

    // 功能：设置地址
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    // 功能：获取总计金额
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    // 功能：设置总计金额
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
