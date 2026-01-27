package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminRoleRequest;
import com.thinking.backendmall.entity.Role;

import java.util.List;

public interface AdminRoleService {
    PageResult<Role> listRoles(String keyword, int page, int size);

    Role createRole(AdminRoleRequest request);

    Role updateRole(Long id, AdminRoleRequest request);

    void deleteRole(Long id);

    List<Long> listRoleMenuIds(Long roleId);

    void updateRoleMenus(Long roleId, List<Long> menuIds);
}
