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
    public Result<UserProfileView> getProfile() {
        return Result.success(userProfileService.getProfile());
    }

    @PutMapping("/me")
    public Result<UserProfileView> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        return Result.success(userProfileService.updateProfile(request));
    }

    @PutMapping("/me/password")
    public Result<Void> changePassword(@Valid @RequestBody UserPasswordChangeRequest request) {
        userProfileService.changePassword(request);
        return Result.success();
    }

    @GetMapping("/me/summary")
    public Result<UserSummaryView> getSummary() {
        return Result.success(userCenterService.getSummary());
    }

    @GetMapping("/me/favorites")
    public Result<List<UserFavoriteView>> listFavorites() {
        return Result.success(userCenterService.listFavorites());
    }

    @PostMapping("/me/favorites")
    public Result<Void> addFavorite(@Valid @RequestBody UserFavoriteRequest request) {
        userCenterService.addFavorite(request.getProductId());
        return Result.success();
    }

    @DeleteMapping("/me/favorites/{productId}")
    public Result<Void> removeFavorite(@PathVariable Long productId) {
        userCenterService.removeFavorite(productId);
        return Result.success();
    }

    @GetMapping("/me/footprints")
    public Result<List<UserFootprintView>> listFootprints() {
        return Result.success(userCenterService.listFootprints());
    }

    @DeleteMapping("/me/footprints/{productId}")
    public Result<Void> removeFootprint(@PathVariable Long productId) {
        userCenterService.removeFootprint(productId);
        return Result.success();
    }
}
