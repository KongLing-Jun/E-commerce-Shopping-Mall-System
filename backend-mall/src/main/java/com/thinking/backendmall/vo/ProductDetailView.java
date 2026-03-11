package com.thinking.backendmall.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailView {
    private Long id;
    private Long categoryId;
    private String name;
    private String brief;
    private BigDecimal price;
    private Integer stock;
    private String status;
    private String coverUrl;
    private String detailHtml;
    private LocalDateTime createdAt;
    private List<String> images = new ArrayList<>();
    private Map<String, String> specs = new LinkedHashMap<>();

    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取分类id
    public Long getCategoryId() {
        return categoryId;
    }

    // 功能：设置分类id
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    // 功能：获取详情html
    public String getDetailHtml() {
        return detailHtml;
    }

    // 功能：设置详情html
    public void setDetailHtml(String detailHtml) {
        this.detailHtml = detailHtml;
    }

    // 功能：获取createdat
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 功能：设置createdat
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 功能：获取图片
    public List<String> getImages() {
        return images;
    }

    // 功能：设置图片
    public void setImages(List<String> images) {
        this.images = images;
    }

    // 功能：获取specs
    public Map<String, String> getSpecs() {
        return specs;
    }

    // 功能：设置specs
    public void setSpecs(Map<String, String> specs) {
        this.specs = specs;
    }
}
