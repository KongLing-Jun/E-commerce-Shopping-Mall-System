package com.thinking.backendmall.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserFavoriteView {
    private Long productId;
    private String name;
    private String brief;
    private BigDecimal price;
    private Integer stock;
    private String status;
    private String coverUrl;
    private LocalDateTime favoriteAt;

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

    // 功能：获取brief
    public String getBrief() {
        return brief;
    }

    // 功能：设置brief
    public void setBrief(String brief) {
        this.brief = brief;
    }

    // 功能：获取价格
    public BigDecimal getPrice() {
        return price;
    }

    // 功能：设置价格
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // 功能：获取库存
    public Integer getStock() {
        return stock;
    }

    // 功能：设置库存
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    // 功能：获取状态
    public String getStatus() {
        return status;
    }

    // 功能：设置状态
    public void setStatus(String status) {
        this.status = status;
    }

    // 功能：获取封面url
    public String getCoverUrl() {
        return coverUrl;
    }

    // 功能：设置封面url
    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    // 功能：获取收藏at
    public LocalDateTime getFavoriteAt() {
        return favoriteAt;
    }

    // 功能：设置收藏at
    public void setFavoriteAt(LocalDateTime favoriteAt) {
        this.favoriteAt = favoriteAt;
    }
}
