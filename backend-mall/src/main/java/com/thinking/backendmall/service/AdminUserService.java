package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.vo.AdminUserView;

public interface AdminUserService {
    PageResult<AdminUserView> listUsers(String keyword, int page, int size);

    void disableUser(Long userId);

    void resetPassword(Long userId, String newPassword);
}
