package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.dto.AdminResetPasswordRequest;
import com.thinking.backendmall.dto.AdminUserRoleRequest;
import com.thinking.backendmall.dto.AdminUserUpsertRequest;
import com.thinking.backendmall.service.AdminUserService;
import com.thinking.backendmall.service.OperationLogService;
import com.thinking.backendmall.vo.AdminUserView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    @PreAuthorize("@permissionService.hasPerm('admin:users:list')")
    public Result<PageResult<AdminUserView>> listUsers(@RequestParam(required = false) String keyword,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        // Query users by keyword with pagination.
        return Result.success(adminUserService.listUsers(keyword, page, size));
    }

    @PostMapping
    @PreAuthorize("@permissionService.hasPerm('admin:users:create')")
    public Result<Long> createUser(@Valid @RequestBody AdminUserUpsertRequest request) {
        // Create user and record audit log.
        Long userId = adminUserService.createUser(request);
        operationLogService.record("USER_CREATE", "user:" + userId, request.getUsername());
        return Result.success(userId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionService.hasPerm('admin:users:edit')")
    public Result<Void> updateUser(@PathVariable Long id, @Valid @RequestBody AdminUserUpsertRequest request) {
        // Update username/phone/status/role and optional password.
        adminUserService.updateUser(id, request);
        operationLogService.record("USER_UPDATE", "user:" + id, request.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("@permissionService.hasPerm('admin:users:disable')")
    public Result<Void> disableUser(@PathVariable Long id) {
        // Disable user account.
        adminUserService.disableUser(id);
        operationLogService.record("USER_DISABLE", "user:" + id, "status=0");
        return Result.success();
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("@permissionService.hasPerm('admin:users:reset')")
    public Result<Void> resetPassword(@PathVariable Long id,
                                      @RequestBody(required = false) AdminResetPasswordRequest request) {
        // Reset password to given value or default value.
        String newPassword = request == null ? null : request.getNewPassword();
        adminUserService.resetPassword(id, newPassword);
        operationLogService.record("USER_RESET_PASSWORD", "user:" + id, "password reset");
        return Result.success();
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("@permissionService.hasPerm('admin:users:role')")
    public Result<Void> updateUserRole(@PathVariable Long id, @Valid @RequestBody AdminUserRoleRequest request) {
        // Update user role only.
        adminUserService.updateUserRole(id, request.getRoleId());
        operationLogService.record("USER_ROLE", "user:" + id, "role assigned");
        return Result.success();
    }
}
