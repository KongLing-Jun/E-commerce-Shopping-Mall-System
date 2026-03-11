package com.thinking.backendmall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class AdminProductRequest {
    @NotNull(message = "CategoryId is required")
    private Long categoryId;

    @NotBlank(message = "Name is required")
    private String name;

    private String brief;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be non-negative")
    private Integer stock;

    private String status;
    private String coverUrl;
    private String detailHtml;
    private List<String> imageUrls;

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

    // 功能：获取图片urls
    public List<String> getImageUrls() {
        return imageUrls;
    }

    // 功能：设置图片urls
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
