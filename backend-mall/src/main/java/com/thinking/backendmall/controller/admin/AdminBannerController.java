package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.dto.AdminBannerRequest;
import com.thinking.backendmall.entity.Banner;
import com.thinking.backendmall.service.AdminBannerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/banners")
public class AdminBannerController {

    @Autowired
    private AdminBannerService adminBannerService;

    @GetMapping
    @PreAuthorize("@permissionService.hasPerm('admin:banners:list')")
    // 功能：查询轮播图
    public Result<PageResult<Banner>> listBanners(@RequestParam(required = false) Integer status,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminBannerService.listBanners(status, page, size));
    }

    @PostMapping
    @PreAuthorize("@permissionService.hasPerm('admin:banners:edit')")
    // 功能：创建轮播图
    public Result<Banner> createBanner(@Valid @RequestBody AdminBannerRequest request) {
        return Result.success(adminBannerService.createBanner(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionService.hasPerm('admin:banners:edit')")
    // 功能：更新轮播图
    public Result<Banner> updateBanner(@PathVariable Long id, @Valid @RequestBody AdminBannerRequest request) {
        return Result.success(adminBannerService.updateBanner(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionService.hasPerm('admin:banners:edit')")
    // 功能：删除轮播图
    public Result<Void> deleteBanner(@PathVariable Long id) {
        adminBannerService.deleteBanner(id);
        return Result.success();
    }
}
