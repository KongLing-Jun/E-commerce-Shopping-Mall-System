package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("user_footprint")
public class UserFootprint {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long productId;
    private LocalDateTime viewedAt;

    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取用户id
    public Long getUserId() {
        return userId;
    }

    // 功能：设置用户id
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // 功能：获取商品id
    public Long getProductId() {
        return productId;
    }

    // 功能：设置商品id
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 功能：获取viewedat
    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    // 功能：设置viewedat
    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
}
