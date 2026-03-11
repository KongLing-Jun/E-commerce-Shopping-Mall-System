package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminRoleRequest;
import com.thinking.backendmall.entity.Role;
import com.thinking.backendmall.entity.RoleMenu;
import com.thinking.backendmall.entity.User;
import com.thinking.backendmall.repository.RoleMenuRepository;
import com.thinking.backendmall.repository.RoleRepository;
import com.thinking.backendmall.repository.UserRepository;
import com.thinking.backendmall.service.AdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminRoleServiceImpl implements AdminRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMenuRepository roleMenuRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    // 功能：查询角色
    public PageResult<Role> listRoles(String keyword, int page, int size) {
        Page<Role> pageResult = new Page<>(page + 1L, size);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Role::getRoleKey, keyword).or().like(Role::getRoleName, keyword));
        }
        wrapper.orderByAsc(Role::getId);
        roleRepository.selectPage(pageResult, wrapper);
        boolean last = pageResult.getCurrent() >= pageResult.getPages();
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), pageResult.getPages(), page, size, last);
    }

    @Override
    // 功能：创建角色
    public Role createRole(AdminRoleRequest request) {
        String roleKey = normalizeRoleKey(request.getRoleKey());
        // 功能：处理ensure角色keyavailable
        ensureRoleKeyAvailable(roleKey, null);
        Role role = new Role();
        role.setRoleKey(roleKey);
        role.setRoleName(request.getRoleName().trim());
        roleRepository.insert(role);
        return role;
    }

    @Override
    // 功能：更新角色
    public Role updateRole(Long id, AdminRoleRequest request) {
        Role role = roleRepository.selectById(id);
        if (role == null) {
            throw new BusinessException(404, "Role not found");
        }
        String roleKey = normalizeRoleKey(request.getRoleKey());
        if (isBuiltInRole(role.getRoleKey()) && !role.getRoleKey().equals(roleKey)) {
            throw new BusinessException(400, "Built-in role key cannot be changed");
        }
        // 功能：处理ensure角色keyavailable
        ensureRoleKeyAvailable(roleKey, id);
        role.setRoleKey(roleKey);
        role.setRoleName(request.getRoleName().trim());
        roleRepository.updateById(role);
        return role;
    }

    @Override
    // 功能：删除角色
    public void deleteRole(Long id) {
        Role role = roleRepository.selectById(id);
        if (role == null) {
            throw new BusinessException(404, "Role not found");
        }
        if (isBuiltInRole(role.getRoleKey())) {
            throw new BusinessException(400, "Built-in roles cannot be deleted");
        }
        Long userCount = userRepository.selectCount(new LambdaQueryWrapper<User>().eq(User::getRoleId, id));
        if (userCount != null && userCount > 0) {
            throw new BusinessException(400, "Role is assigned to users");
        }
        roleMenuRepository.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, id));
        roleRepository.deleteById(id);
    }

    @Override
    // 功能：查询角色菜单ids
    public List<Long> listRoleMenuIds(Long roleId) {
        Role role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(404, "Role not found");
        }
        List<RoleMenu> roleMenus = roleMenuRepository.selectList(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId));
        List<Long> ids = new ArrayList<>();
        if (roleMenus != null) {
            for (RoleMenu roleMenu : roleMenus) {
                ids.add(roleMenu.getMenuId());
            }
        }
        return ids;
    }

    @Override
    // 功能：更新角色菜单
    public void updateRoleMenus(Long roleId, List<Long> menuIds) {
        Role role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(404, "Role not found");
        }
        roleMenuRepository.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        Set<Long> uniqueMenuIds = new HashSet<>(menuIds);
        for (Long menuId : uniqueMenuIds) {
            if (menuId == null) {
                continue;
            }
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuRepository.insert(roleMenu);
        }
    }

    // 功能：处理ensure角色keyavailable
    private void ensureRoleKeyAvailable(String roleKey, Long currentId) {
        Role existing = roleRepository.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, roleKey));
        if (existing != null && (currentId == null || !existing.getId().equals(currentId))) {
            throw new BusinessException(400, "Role key already exists");
        }
    }

    // 功能：处理normalize角色key
    private String normalizeRoleKey(String roleKey) {
        if (roleKey == null || roleKey.isBlank()) {
            throw new BusinessException(400, "Role key is required");
        }
        return roleKey.trim().toUpperCase();
    }

    // 功能：判断builtin角色
    private boolean isBuiltInRole(String roleKey) {
        return "ADMIN".equalsIgnoreCase(roleKey) || "USER".equalsIgnoreCase(roleKey);
    }
}
