package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.dto.AdminProductRequest;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.service.AdminProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    @Autowired
    private AdminProductService adminProductService;

    @GetMapping
    @PreAuthorize("@permissionService.hasPerm('admin:products:list')")
    public Result<PageResult<Product>> listProducts(@RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) Long categoryId,
                                                    @RequestParam(required = false) String status,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminProductService.listProducts(keyword, categoryId, status, page, size));
    }

    @PostMapping
    @PreAuthorize("@permissionService.hasPerm('admin:products:on')")
    public Result<Product> createProduct(@Valid @RequestBody AdminProductRequest request) {
        return Result.success(adminProductService.createProduct(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionService.hasPerm('admin:products:on')")
    public Result<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody AdminProductRequest request) {
        return Result.success(adminProductService.updateProduct(id, request));
    }

    @PutMapping("/{id}/on")
    @PreAuthorize("@permissionService.hasPerm('admin:products:on')")
    public Result<Void> enableProduct(@PathVariable Long id) {
        adminProductService.updateStatus(id, "ON");
        return Result.success();
    }

    @PutMapping("/{id}/off")
    @PreAuthorize("@permissionService.hasPerm('admin:products:off')")
    public Result<Void> disableProduct(@PathVariable Long id) {
        adminProductService.updateStatus(id, "OFF");
        return Result.success();
    }
}
