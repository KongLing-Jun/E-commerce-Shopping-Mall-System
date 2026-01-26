package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.dto.MenuRequest;
import com.thinking.backendmall.service.AdminMenuService;
import com.thinking.backendmall.vo.MenuTreeNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menus")
public class AdminMenuController {

    @Autowired
    private AdminMenuService adminMenuService;

    @GetMapping("/tree")
    public Result<List<MenuTreeNode>> listMenuTree() {
        return Result.success(adminMenuService.listMenuTree());
    }

    @PostMapping
    public Result<Void> createMenu(@Valid @RequestBody MenuRequest request) {
        adminMenuService.createMenu(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuRequest request) {
        adminMenuService.updateMenu(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        adminMenuService.deleteMenu(id);
        return Result.success();
    }
}
