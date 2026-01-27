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
    public Role createRole(AdminRoleRequest request) {
        String roleKey = normalizeRoleKey(request.getRoleKey());
        ensureRoleKeyAvailable(roleKey, null);
        Role role = new Role();
        role.setRoleKey(roleKey);
        role.setRoleName(request.getRoleName().trim());
        roleRepository.insert(role);
        return role;
    }

    @Override
    public Role updateRole(Long id, AdminRoleRequest request) {
        Role role = roleRepository.selectById(id);
        if (role == null) {
            throw new BusinessException(404, "Role not found");
        }
        String roleKey = normalizeRoleKey(request.getRoleKey());
        if (isBuiltInRole(role.getRoleKey()) && !role.getRoleKey().equals(roleKey)) {
            throw new BusinessException(400, "Built-in role key cannot be changed");
        }
        ensureRoleKeyAvailable(roleKey, id);
        role.setRoleKey(roleKey);
        role.setRoleName(request.getRoleName().trim());
        roleRepository.updateById(role);
        return role;
    }

    @Override
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

    private void ensureRoleKeyAvailable(String roleKey, Long currentId) {
        Role existing = roleRepository.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, roleKey));
        if (existing != null && (currentId == null || !existing.getId().equals(currentId))) {
            throw new BusinessException(400, "Role key already exists");
        }
    }

    private String normalizeRoleKey(String roleKey) {
        if (roleKey == null || roleKey.isBlank()) {
            throw new BusinessException(400, "Role key is required");
        }
        return roleKey.trim().toUpperCase();
    }

    private boolean isBuiltInRole(String roleKey) {
        return "ADMIN".equalsIgnoreCase(roleKey) || "USER".equalsIgnoreCase(roleKey);
    }
}
