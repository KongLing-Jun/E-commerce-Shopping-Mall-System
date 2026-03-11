package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminRoleRequest;
import com.thinking.backendmall.entity.Role;

import java.util.List;

public interface AdminRoleService {
    // 功能：查询角色
    PageResult<Role> listRoles(String keyword, int page, int size);

    // 功能：创建角色
    Role createRole(AdminRoleRequest request);

    // 功能：更新角色
    Role updateRole(Long id, AdminRoleRequest request);

    // 功能：删除角色
    void deleteRole(Long id);

    // 功能：查询角色菜单ids
    List<Long> listRoleMenuIds(Long roleId);

    // 功能：更新角色菜单
    void updateRoleMenus(Long roleId, List<Long> menuIds);
}
