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

    @NotNull(message = "Sort is required")
    private Integer sort;

    @NotNull(message = "Status is required")
    private Integer status;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getLinkTarget() {
        return linkTarget;
    }

    public void setLinkTarget(String linkTarget) {
        this.linkTarget = linkTarget;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
