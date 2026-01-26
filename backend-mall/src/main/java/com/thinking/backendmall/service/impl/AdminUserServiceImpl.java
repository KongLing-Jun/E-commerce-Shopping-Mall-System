package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.entity.Role;
import com.thinking.backendmall.entity.User;
import com.thinking.backendmall.repository.RoleRepository;
import com.thinking.backendmall.repository.UserRepository;
import com.thinking.backendmall.service.AdminUserService;
import com.thinking.backendmall.vo.AdminUserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public PageResult<AdminUserView> listUsers(String keyword, int page, int size) {
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
        Map<Long, String> roleMap = loadRoleMap(users);

        List<AdminUserView> views = new ArrayList<>();
        for (User user : users) {
            AdminUserView view = new AdminUserView();
            view.setId(user.getId());
            view.setUsername(user.getUsername());
            view.setPhone(user.getPhone());
            view.setStatus(user.getStatus());
            view.setRoleKey(roleMap.get(user.getRoleId()));
            view.setCreatedAt(user.getCreatedAt());
            views.add(view);
        }

        boolean last = pageResult.getCurrent() >= pageResult.getPages();
        return new PageResult<>(views, pageResult.getTotal(), pageResult.getPages(), page, size, last);
    }

    @Override
    public void disableUser(Long userId) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "User not found");
        }
        user.setStatus(0);
        userRepository.updateById(user);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "User not found");
        }
        String password = newPassword == null || newPassword.isBlank() ? DEFAULT_PASSWORD : newPassword;
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.updateById(user);
    }

    private Map<Long, String> loadRoleMap(List<User> users) {
        Map<Long, String> map = new HashMap<>();
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
                map.put(role.getId(), role.getRoleKey());
            }
        }
        return map;
    }
}
