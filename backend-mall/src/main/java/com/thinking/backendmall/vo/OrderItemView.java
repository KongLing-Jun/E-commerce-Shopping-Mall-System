package com.thinking.backendmall.vo;

import java.math.BigDecimal;

public class OrderItemView {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private String image;

    // 功能：获取商品id
    public Long getProductId() {
        return productId;
    }

    // 功能：设置商品id
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 功能：获取商品name
    public String getProductName() {
        return productName;
    }

    // 功能：设置商品name
    public void setProductName(String productName) {
        this.productName = productName;
    }

    // 功能：获取价格
    public BigDecimal getPrice() {
        return price;
    }

    // 功能：设置价格
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // 功能：获取quantity
    public Integer getQuantity() {
        return quantity;
    }

    // 功能：设置quantity
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // 功能：获取图片
    public String getImage() {
        return image;
    }

    // 功能：设置图片
    public void setImage(String image) {
        this.image = image;
    }
}
