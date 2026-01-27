package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.dto.AdminCategoryRequest;
import com.thinking.backendmall.entity.Category;
import com.thinking.backendmall.service.AdminCategoryService;
import com.thinking.backendmall.service.OperationLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService adminCategoryService;

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    @PreAuthorize("@permissionService.hasPerm('admin:categories:list')")
    public Result<PageResult<Category>> listCategories(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) Integer status,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminCategoryService.listCategories(keyword, status, page, size));
    }

    @PostMapping
    @PreAuthorize("@permissionService.hasPerm('admin:categories:edit')")
    public Result<Category> createCategory(@Valid @RequestBody AdminCategoryRequest request) {
        Category category = adminCategoryService.createCategory(request);
        operationLogService.record("CATEGORY_CREATE", "category:" + category.getId(), category.getName());
        return Result.success(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionService.hasPerm('admin:categories:edit')")
    public Result<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody AdminCategoryRequest request) {
        Category category = adminCategoryService.updateCategory(id, request);
        operationLogService.record("CATEGORY_UPDATE", "category:" + id, category.getName());
        return Result.success(category);
    }

    @PutMapping("/{id}/on")
    @PreAuthorize("@permissionService.hasPerm('admin:categories:edit')")
    public Result<Void> enableCategory(@PathVariable Long id) {
        adminCategoryService.updateStatus(id, 1);
        operationLogService.record("CATEGORY_ON", "category:" + id, "status=ON");
        return Result.success();
    }

    @PutMapping("/{id}/off")
    @PreAuthorize("@permissionService.hasPerm('admin:categories:edit')")
    public Result<Void> disableCategory(@PathVariable Long id) {
        adminCategoryService.updateStatus(id, 0);
        operationLogService.record("CATEGORY_OFF", "category:" + id, "status=OFF");
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionService.hasPerm('admin:categories:delete')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        adminCategoryService.deleteCategory(id);
        operationLogService.record("CATEGORY_DELETE", "category:" + id, "deleted");
        return Result.success();
    }
}
