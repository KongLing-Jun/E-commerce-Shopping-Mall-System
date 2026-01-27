package com.thinking.backendmall.service;

import com.thinking.backendmall.dto.UserPasswordChangeRequest;
import com.thinking.backendmall.dto.UserProfileUpdateRequest;
import com.thinking.backendmall.vo.UserProfileView;

public interface UserProfileService {
    UserProfileView getProfile();

    UserProfileView updateProfile(UserProfileUpdateRequest request);

    void changePassword(UserPasswordChangeRequest request);
}
