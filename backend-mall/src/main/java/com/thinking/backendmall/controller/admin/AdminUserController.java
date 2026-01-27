package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.dto.AdminResetPasswordRequest;
import com.thinking.backendmall.dto.AdminUserRoleRequest;
import com.thinking.backendmall.service.AdminUserService;
import com.thinking.backendmall.service.OperationLogService;
import com.thinking.backendmall.vo.AdminUserView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        return Result.success(adminUserService.listUsers(keyword, page, size));
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("@permissionService.hasPerm('admin:users:disable')")
    public Result<Void> disableUser(@PathVariable Long id) {
        adminUserService.disableUser(id);
        return Result.success();
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("@permissionService.hasPerm('admin:users:reset')")
    public Result<Void> resetPassword(@PathVariable Long id,
                                      @RequestBody(required = false) AdminResetPasswordRequest request) {
        String newPassword = request == null ? null : request.getNewPassword();
        adminUserService.resetPassword(id, newPassword);
        return Result.success();
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("@permissionService.hasPerm('admin:users:role')")
    public Result<Void> updateUserRole(@PathVariable Long id, @Valid @RequestBody AdminUserRoleRequest request) {
        adminUserService.updateUserRole(id, request.getRoleId());
        operationLogService.record("USER_ROLE", "user:" + id, "role assigned");
        return Result.success();
    }
}
