package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.config.JwtUtil;
import com.thinking.backendmall.config.security.AuthContext;
import com.thinking.backendmall.dto.UserPasswordChangeRequest;
import com.thinking.backendmall.dto.UserProfileUpdateRequest;
import com.thinking.backendmall.entity.Role;
import com.thinking.backendmall.entity.User;
import com.thinking.backendmall.repository.RoleRepository;
import com.thinking.backendmall.repository.UserRepository;
import com.thinking.backendmall.service.UserProfileService;
import com.thinking.backendmall.vo.UserProfileView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserProfileServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserProfileView getProfile() {
        User user = requireUser();
        Role role = roleRepository.selectById(user.getRoleId());
        return toView(user, role, null);
    }

    @Override
    public UserProfileView updateProfile(UserProfileUpdateRequest request) {
        User user = requireUser();
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String phone = request.getPhone() == null ? "" : request.getPhone().trim();
        if (username.isBlank() || phone.isBlank()) {
            throw new BusinessException(400, "Username and phone are required");
        }

        if (!username.equals(user.getUsername())) {
            User existingByUsername = userRepository.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username)
                    .ne(User::getId, user.getId()));
            if (existingByUsername != null) {
                throw new BusinessException(400, "Username already exists");
            }
        }
        if (!phone.equals(user.getPhone())) {
            User existingByPhone = userRepository.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, phone)
                    .ne(User::getId, user.getId()));
            if (existingByPhone != null) {
                throw new BusinessException(400, "Phone already exists");
            }
        }

        user.setUsername(username);
        user.setPhone(phone);
        userRepository.updateById(user);

        Role role = roleRepository.selectById(user.getRoleId());
        String roleKey = role != null ? role.getRoleKey() : AuthContext.getRoleKey();
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), roleKey == null ? "USER" : roleKey);
        UserProfileView view = toView(user, role, token);
        return view;
    }

    @Override
    public void changePassword(UserPasswordChangeRequest request) {
        User user = requireUser();
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(400, "Passwords do not match");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessException(400, "Old password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.updateById(user);
    }

    private User requireUser() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "Unauthorized");
        }
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "User not found");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(403, "User is disabled");
        }
        return user;
    }

    private UserProfileView toView(User user, Role role, String token) {
        UserProfileView view = new UserProfileView();
        view.setId(user.getId());
        view.setUsername(user.getUsername());
        view.setPhone(user.getPhone());
        if (role != null) {
            view.setRoleKey(role.getRoleKey());
            view.setRoleName(role.getRoleName());
        } else {
            view.setRoleKey(AuthContext.getRoleKey());
        }
        view.setCreatedAt(user.getCreatedAt());
        view.setToken(token);
        return view;
    }
}
