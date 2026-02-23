package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminUserUpsertRequest;
import com.thinking.backendmall.vo.AdminUserView;

public interface AdminUserService {
    PageResult<AdminUserView> listUsers(String keyword, int page, int size);

    Long createUser(AdminUserUpsertRequest request);

    void updateUser(Long userId, AdminUserUpsertRequest request);

    void disableUser(Long userId);

    void resetPassword(Long userId, String newPassword);

    void updateUserRole(Long userId, Long roleId);
}
