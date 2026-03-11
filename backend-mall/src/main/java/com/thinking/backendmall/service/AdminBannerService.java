package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminBannerRequest;
import com.thinking.backendmall.entity.Banner;

public interface AdminBannerService {
    // 功能：查询轮播图
    PageResult<Banner> listBanners(Integer status, int page, int size);

    // 功能：创建轮播图
    Banner createBanner(AdminBannerRequest request);

    // 功能：更新轮播图
    Banner updateBanner(Long id, AdminBannerRequest request);

    // 功能：删除轮播图
    void deleteBanner(Long id);
}
