package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@TableName("cart_item")
public class CartItem {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long productId;
    private Integer quantity;
    private Integer checked; // 0/1

    private LocalDateTime createdAt;

    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取createdat
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 功能：设置createdat
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 功能：获取checked
    public Integer getChecked() {
        return checked;
    }

    // 功能：设置checked
    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    // 功能：获取quantity
    public Integer getQuantity() {
        return quantity;
    }

    // 功能：设置quantity
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // 功能：获取商品id
    public Long getProductId() {
        return productId;
    }

    // 功能：设置商品id
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 功能：获取用户id
    public Long getUserId() {
        return userId;
    }

    // 功能：设置用户id
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
