package com.thinking.backendmall.service;

import com.thinking.backendmall.dto.UserPasswordChangeRequest;
import com.thinking.backendmall.dto.UserProfileUpdateRequest;
import com.thinking.backendmall.vo.UserProfileView;

public interface UserProfileService {
    // 功能：获取个人信息
    UserProfileView getProfile();

    // 功能：更新个人信息
    UserProfileView updateProfile(UserProfileUpdateRequest request);

    // 功能：修改密码
    void changePassword(UserPasswordChangeRequest request);
}
