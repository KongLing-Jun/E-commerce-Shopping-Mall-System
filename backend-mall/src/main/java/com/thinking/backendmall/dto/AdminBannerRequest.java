package com.thinking.backendmall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdminBannerRequest {
    @NotBlank(message = "ImageUrl is required")
    private String imageUrl;

    @NotBlank(message = "LinkType is required")
    private String linkType;

    @NotBlank(message = "LinkTarget is required")
    private String linkTarget;

    private String title;
    private String subtitle;
    private String buttonText;

    @NotNull(message = "Sort is required")
    private Integer sort;

    @NotNull(message = "Status is required")
    private Integer status;

    // 功能：获取轮播图图片地址。
    public String getImageUrl() {
        return imageUrl;
    }

    // 功能：设置轮播图图片地址。
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // 功能：获取跳转类型。
    public String getLinkType() {
        return linkType;
    }

    // 功能：设置跳转类型。
    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    // 功能：获取跳转目标。
    public String getLinkTarget() {
        return linkTarget;
    }

    // 功能：设置跳转目标。
    public void setLinkTarget(String linkTarget) {
        this.linkTarget = linkTarget;
    }

    // 功能：获取轮播图标题。
    public String getTitle() {
        return title;
    }

    // 功能：设置轮播图标题。
    public void setTitle(String title) {
        this.title = title;
    }

    // 功能：获取轮播图副标题。
    public String getSubtitle() {
        return subtitle;
    }

    // 功能：设置轮播图副标题。
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    // 功能：获取轮播图按钮文案。
    public String getButtonText() {
        return buttonText;
    }

    // 功能：设置轮播图按钮文案。
    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    // 功能：获取排序值。
    public Integer getSort() {
        return sort;
    }

    // 功能：设置排序值。
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    // 功能：获取状态值。
    public Integer getStatus() {
        return status;
    }

    // 功能：设置状态值。
    public void setStatus(Integer status) {
        this.status = status;
    }
}
