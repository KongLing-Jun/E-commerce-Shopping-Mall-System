package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.dto.AdminRoleRequest;
import com.thinking.backendmall.dto.RoleMenuUpdateRequest;
import com.thinking.backendmall.entity.Role;
import com.thinking.backendmall.service.AdminRoleService;
import com.thinking.backendmall.service.OperationLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
public class AdminRoleController {

    @Autowired
    private AdminRoleService adminRoleService;

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    @PreAuthorize("@permissionService.hasPerm('admin:roles:list')")
    public Result<PageResult<Role>> listRoles(@RequestParam(required = false) String keyword,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminRoleService.listRoles(keyword, page, size));
    }

    @PostMapping
    @PreAuthorize("@permissionService.hasPerm('admin:roles:edit')")
    public Result<Role> createRole(@Valid @RequestBody AdminRoleRequest request) {
        Role role = adminRoleService.createRole(request);
        operationLogService.record("ROLE_CREATE", "role:" + role.getId(), role.getRoleKey());
        return Result.success(role);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionService.hasPerm('admin:roles:edit')")
    public Result<Role> updateRole(@PathVariable Long id, @Valid @RequestBody AdminRoleRequest request) {
        Role role = adminRoleService.updateRole(id, request);
        operationLogService.record("ROLE_UPDATE", "role:" + id, role.getRoleKey());
        return Result.success(role);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionService.hasPerm('admin:roles:delete')")
    public Result<Void> deleteRole(@PathVariable Long id) {
        adminRoleService.deleteRole(id);
        operationLogService.record("ROLE_DELETE", "role:" + id, "deleted");
        return Result.success();
    }

    @GetMapping("/{id}/menus")
    @PreAuthorize("@permissionService.hasPerm('admin:roles:edit')")
    public Result<List<Long>> listRoleMenus(@PathVariable Long id) {
        return Result.success(adminRoleService.listRoleMenuIds(id));
    }

    @PutMapping("/{id}/menus")
    @PreAuthorize("@permissionService.hasPerm('admin:roles:edit')")
    public Result<Void> updateRoleMenus(@PathVariable Long id, @Valid @RequestBody RoleMenuUpdateRequest request) {
        adminRoleService.updateRoleMenus(id, request.getMenuIds());
        operationLogService.record("ROLE_MENUS", "role:" + id, "menus updated");
        return Result.success();
    }
}
