package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminBannerRequest;
import com.thinking.backendmall.entity.Banner;

public interface AdminBannerService {
    PageResult<Banner> listBanners(Integer status, int page, int size);

    Banner createBanner(AdminBannerRequest request);

    Banner updateBanner(Long id, AdminBannerRequest request);

    void deleteBanner(Long id);
}
