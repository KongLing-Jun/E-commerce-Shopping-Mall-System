package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("banner")
public class Banner {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String imageUrl;
    private String linkType; // PRODUCT/URL
    private String linkTarget;
    private String title;
    private String subtitle;
    private String buttonText;
    private Integer sort;
    private Integer status; // 1启用 0禁用

    // 功能：获取轮播图ID。
    public Long getId() {
        return id;
    }

    // 功能：设置轮播图ID。
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取轮播图状态。
    public Integer getStatus() {
        return status;
    }

    // 功能：设置轮播图状态。
    public void setStatus(Integer status) {
        this.status = status;
    }

    // 功能：获取轮播图排序。
    public Integer getSort() {
        return sort;
    }

    // 功能：设置轮播图排序。
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    // 功能：获取跳转目标。
    public String getLinkTarget() {
        return linkTarget;
    }

    // 功能：设置跳转目标。
    public void setLinkTarget(String linkTarget) {
        this.linkTarget = linkTarget;
    }

    // 功能：获取跳转类型。
    public String getLinkType() {
        return linkType;
    }

    // 功能：设置跳转类型。
    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    // 功能：获取轮播图图片地址。
    public String getImageUrl() {
        return imageUrl;
    }

    // 功能：设置轮播图图片地址。
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
}
