package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminUserUpsertRequest;
import com.thinking.backendmall.entity.Role;
import com.thinking.backendmall.entity.User;
import com.thinking.backendmall.repository.RoleRepository;
import com.thinking.backendmall.repository.UserRepository;
import com.thinking.backendmall.service.AdminUserService;
import com.thinking.backendmall.vo.AdminUserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    // 功能：查询用户
    public PageResult<AdminUserView> listUsers(String keyword, int page, int size) {
        // Page users by username/phone keyword.
        Page<User> pageResult = new Page<>(page + 1L, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(query -> query
                    .like(User::getUsername, keyword)
                    .or()
                    .like(User::getPhone, keyword));
        }
        wrapper.orderByDesc(User::getCreatedAt);
        userRepository.selectPage(pageResult, wrapper);

        List<User> users = pageResult.getRecords();
        Map<Long, Role> roleMap = loadRoles(users);

        List<AdminUserView> views = new ArrayList<>();
        for (User user : users) {
            AdminUserView view = new AdminUserView();
            view.setId(user.getId());
            view.setUsername(user.getUsername());
            view.setPhone(user.getPhone());
            view.setStatus(user.getStatus());
            Role role = roleMap.get(user.getRoleId());
            if (role != null) {
                view.setRoleId(role.getId());
                view.setRoleKey(role.getRoleKey());
                view.setRoleName(role.getRoleName());
            }
            view.setCreatedAt(user.getCreatedAt());
            views.add(view);
        }

        boolean last = pageResult.getCurrent() >= pageResult.getPages();
        return new PageResult<>(views, pageResult.getTotal(), pageResult.getPages(), page, size, last);
    }

    @Override
    // 功能：创建用户
    public Long createUser(AdminUserUpsertRequest request) {
        // Validate role and uniqueness before create.
        Role role = ensureRole(request.getRoleId());
        // 功能：处理assertunique用户名
        assertUniqueUsername(request.getUsername(), null);
        // 功能：处理assertunique手机号
        assertUniquePhone(request.getPhone(), null);

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPhone(request.getPhone().trim());
        user.setPasswordHash(passwordEncoder.encode(resolvePassword(request.getPassword())));
        user.setRoleId(role.getId());
        user.setStatus(normalizeStatus(request.getStatus()));
        user.setCreatedAt(LocalDateTime.now());
        userRepository.insert(user);
        return user.getId();
    }

    @Override
    // 功能：更新用户
    public void updateUser(Long userId, AdminUserUpsertRequest request) {
        // Update base profile and optional password.
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "User not found");
        }

        Role role = ensureRole(request.getRoleId());
        // 功能：处理assertunique用户名
        assertUniqueUsername(request.getUsername(), userId);
        // 功能：处理assertunique手机号
        assertUniquePhone(request.getPhone(), userId);

        user.setUsername(request.getUsername().trim());
        user.setPhone(request.getPhone().trim());
        user.setRoleId(role.getId());
        user.setStatus(normalizeStatus(request.getStatus()));
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword().trim()));
        }
        userRepository.updateById(user);
    }

    @Override
    // 功能：禁用用户
    public void disableUser(Long userId) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "User not found");
        }
        user.setStatus(0);
        userRepository.updateById(user);
    }

    @Override
    // 功能：重置密码
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "User not found");
        }
        String password = newPassword == null || newPassword.isBlank() ? DEFAULT_PASSWORD : newPassword;
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.updateById(user);
    }

    @Override
    // 功能：更新用户角色
    public void updateUserRole(Long userId, Long roleId) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "User not found");
        }
        if (roleId == null) {
            throw new BusinessException(400, "RoleId is required");
        }
        Role role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(404, "Role not found");
        }
        user.setRoleId(roleId);
        userRepository.updateById(user);
    }

    // 功能：加载角色
    private Map<Long, Role> loadRoles(List<User> users) {
        Map<Long, Role> map = new HashMap<>();
        if (users.isEmpty()) {
            return map;
        }
        List<Long> roleIds = new ArrayList<>();
        for (User user : users) {
            if (user.getRoleId() != null) {
                roleIds.add(user.getRoleId());
            }
        }
        if (!roleIds.isEmpty()) {
            for (Role role : roleRepository.selectBatchIds(roleIds)) {
                map.put(role.getId(), role);
            }
        }
        return map;
    }

    // 功能：处理ensure角色
    private Role ensureRole(Long roleId) {
        if (roleId == null) {
            throw new BusinessException(400, "Role is required");
        }
        Role role = roleRepository.selectById(roleId);
        if (role == null) {
            throw new BusinessException(404, "Role not found");
        }
        return role;
    }

    // 功能：处理resolve密码
    private String resolvePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return DEFAULT_PASSWORD;
        }
        return rawPassword.trim();
    }

    // 功能：处理normalize状态
    private Integer normalizeStatus(Integer status) {
        // Default to enabled when status is not provided.
        return status != null && status == 0 ? 0 : 1;
    }

    // 功能：处理assertunique用户名
    private void assertUniqueUsername(String username, Long excludeUserId) {
        if (username == null || username.isBlank()) {
            throw new BusinessException(400, "Username is required");
        }
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username.trim());
        if (excludeUserId != null) {
            query.ne(User::getId, excludeUserId);
        }
        Long count = userRepository.selectCount(query);
        if (count != null && count > 0) {
            throw new BusinessException(400, "Username already exists");
        }
    }

    // 功能：处理assertunique手机号
    private void assertUniquePhone(String phone, Long excludeUserId) {
        if (phone == null || phone.isBlank()) {
            throw new BusinessException(400, "Phone is required");
        }
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone.trim());
        if (excludeUserId != null) {
            query.ne(User::getId, excludeUserId);
        }
        Long count = userRepository.selectCount(query);
        if (count != null && count > 0) {
            throw new BusinessException(400, "Phone already exists");
        }
    }
}
