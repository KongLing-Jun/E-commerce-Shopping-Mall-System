package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

@TableName("order_item")
public class OrderItem {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long orderId;
    private Long productId;
    private String productNameSnapshot;
    private BigDecimal priceSnapshot;
    private Integer quantity;
    private String imageSnapshot;
    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取订单id
    public Long getOrderId() {
        return orderId;
    }

    // 功能：设置订单id
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    // 功能：获取商品id
    public Long getProductId() {
        return productId;
    }

    // 功能：设置商品id
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 功能：获取商品name快照
    public String getProductNameSnapshot() {
        return productNameSnapshot;
    }

    // 功能：设置商品name快照
    public void setProductNameSnapshot(String productNameSnapshot) {
        this.productNameSnapshot = productNameSnapshot;
    }

    // 功能：获取价格快照
    public BigDecimal getPriceSnapshot() {
        return priceSnapshot;
    }

    // 功能：设置价格快照
    public void setPriceSnapshot(BigDecimal priceSnapshot) {
        this.priceSnapshot = priceSnapshot;
    }

    // 功能：获取quantity
    public Integer getQuantity() {
        return quantity;
    }

    // 功能：设置quantity
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // 功能：获取图片快照
    public String getImageSnapshot() {
        return imageSnapshot;
    }

    // 功能：设置图片快照
    public void setImageSnapshot(String imageSnapshot) {
        this.imageSnapshot = imageSnapshot;
    }
}
