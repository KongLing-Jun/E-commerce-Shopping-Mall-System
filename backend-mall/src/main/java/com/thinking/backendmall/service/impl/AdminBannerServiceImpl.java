package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinking.backendmall.common.CacheKeys;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminBannerRequest;
import com.thinking.backendmall.entity.Banner;
import com.thinking.backendmall.repository.BannerRepository;
import com.thinking.backendmall.service.AdminBannerService;
import com.thinking.backendmall.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminBannerServiceImpl implements AdminBannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private CacheService cacheService;

    @Override
    public PageResult<Banner> listBanners(Integer status, int page, int size) {
        Page<Banner> pageResult = new Page<>(page + 1L, size);
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Banner::getStatus, status);
        }
        wrapper.orderByAsc(Banner::getSort).orderByAsc(Banner::getId);
        bannerRepository.selectPage(pageResult, wrapper);
        boolean last = pageResult.getCurrent() >= pageResult.getPages();
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), pageResult.getPages(), page, size, last);
    }

    @Override
    public Banner createBanner(AdminBannerRequest request) {
        Banner banner = new Banner();
        banner.setImageUrl(request.getImageUrl());
        banner.setLinkType(request.getLinkType());
        banner.setLinkTarget(request.getLinkTarget());
        banner.setSort(request.getSort());
        banner.setStatus(request.getStatus());
        bannerRepository.insert(banner);
        cacheService.delete(CacheKeys.HOME_BANNERS);
        return banner;
    }

    @Override
    public Banner updateBanner(Long id, AdminBannerRequest request) {
        Banner banner = bannerRepository.selectById(id);
        if (banner == null) {
            throw new BusinessException(404, "Banner not found");
        }
        banner.setImageUrl(request.getImageUrl());
        banner.setLinkType(request.getLinkType());
        banner.setLinkTarget(request.getLinkTarget());
        banner.setSort(request.getSort());
        banner.setStatus(request.getStatus());
        bannerRepository.updateById(banner);
        cacheService.delete(CacheKeys.HOME_BANNERS);
        return banner;
    }

    @Override
    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.selectById(id);
        if (banner == null) {
            throw new BusinessException(404, "Banner not found");
        }
        bannerRepository.deleteById(id);
        cacheService.delete(CacheKeys.HOME_BANNERS);
    }
}
