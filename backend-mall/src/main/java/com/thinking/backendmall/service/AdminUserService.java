package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminUserUpsertRequest;
import com.thinking.backendmall.vo.AdminUserView;

public interface AdminUserService {
    // 功能：查询用户
    PageResult<AdminUserView> listUsers(String keyword, int page, int size);

    // 功能：创建用户
    Long createUser(AdminUserUpsertRequest request);

    // 功能：更新用户
    void updateUser(Long userId, AdminUserUpsertRequest request);

    // 功能：禁用用户
    void disableUser(Long userId);

    // 功能：重置密码
    void resetPassword(Long userId, String newPassword);

    // 功能：更新用户角色
    void updateUserRole(Long userId, Long roleId);
}
