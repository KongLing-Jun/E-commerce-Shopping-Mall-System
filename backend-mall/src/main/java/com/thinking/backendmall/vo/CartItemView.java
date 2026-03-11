package com.thinking.backendmall.vo;

import java.math.BigDecimal;

public class CartItemView {
    private Long cartItemId;
    private Long productId;
    private String name;
    private BigDecimal price;
    private String image;
    private Integer quantity;
    private Integer checked;

    // 功能：获取购物车明细id
    public Long getCartItemId() {
        return cartItemId;
    }

    // 功能：设置购物车明细id
    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    // 功能：获取商品id
    public Long getProductId() {
        return productId;
    }

    // 功能：设置商品id
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 功能：获取name
    public String getName() {
        return name;
    }

    // 功能：设置name
    public void setName(String name) {
        this.name = name;
    }

    // 功能：获取价格
    public BigDecimal getPrice() {
        return price;
    }

    // 功能：设置价格
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // 功能：获取图片
    public String getImage() {
        return image;
    }

    // 功能：设置图片
    public void setImage(String image) {
        this.image = image;
    }

    // 功能：获取quantity
    public Integer getQuantity() {
        return quantity;
    }

    // 功能：设置quantity
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // 功能：获取checked
    public Integer getChecked() {
        return checked;
    }

    // 功能：设置checked
    public void setChecked(Integer checked) {
        this.checked = checked;
    }
}
