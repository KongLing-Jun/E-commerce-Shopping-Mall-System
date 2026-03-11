package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.dto.UserPasswordChangeRequest;
import com.thinking.backendmall.dto.UserProfileUpdateRequest;
import com.thinking.backendmall.dto.UserFavoriteRequest;
import com.thinking.backendmall.service.UserCenterService;
import com.thinking.backendmall.service.UserProfileService;
import com.thinking.backendmall.vo.UserFavoriteView;
import com.thinking.backendmall.vo.UserFootprintView;
import com.thinking.backendmall.vo.UserProfileView;
import com.thinking.backendmall.vo.UserSummaryView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserCenterService userCenterService;

    @GetMapping("/me")
    // 功能：获取个人信息
    public Result<UserProfileView> getProfile() {
        return Result.success(userProfileService.getProfile());
    }

    @PutMapping("/me")
    // 功能：更新个人信息
    public Result<UserProfileView> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        return Result.success(userProfileService.updateProfile(request));
    }

    @PutMapping("/me/password")
    // 功能：修改密码
    public Result<Void> changePassword(@Valid @RequestBody UserPasswordChangeRequest request) {
        userProfileService.changePassword(request);
        return Result.success();
    }

    @GetMapping("/me/summary")
    // 功能：获取汇总
    public Result<UserSummaryView> getSummary() {
        return Result.success(userCenterService.getSummary());
    }

    @GetMapping("/me/favorites")
    // 功能：查询收藏
    public Result<List<UserFavoriteView>> listFavorites() {
        return Result.success(userCenterService.listFavorites());
    }

    @PostMapping("/me/favorites")
    // 功能：新增收藏
    public Result<Void> addFavorite(@Valid @RequestBody UserFavoriteRequest request) {
        userCenterService.addFavorite(request.getProductId());
        return Result.success();
    }

    @DeleteMapping("/me/favorites/{productId}")
    // 功能：移除收藏
    public Result<Void> removeFavorite(@PathVariable Long productId) {
        userCenterService.removeFavorite(productId);
        return Result.success();
    }

    @GetMapping("/me/footprints")
    // 功能：查询足迹
    public Result<List<UserFootprintView>> listFootprints() {
        return Result.success(userCenterService.listFootprints());
    }

    @DeleteMapping("/me/footprints/{productId}")
    // 功能：移除足迹
    public Result<Void> removeFootprint(@PathVariable Long productId) {
        userCenterService.removeFootprint(productId);
        return Result.success();
    }
}
