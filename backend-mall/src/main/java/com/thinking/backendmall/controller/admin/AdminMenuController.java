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
    // 功能：查询菜单tree
    public Result<List<MenuTreeNode>> listMenuTree() {
        return Result.success(adminMenuService.listMenuTree());
    }

    @PostMapping
    // 功能：创建菜单
    public Result<Void> createMenu(@Valid @RequestBody MenuRequest request) {
        adminMenuService.createMenu(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    // 功能：更新菜单
    public Result<Void> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuRequest request) {
        adminMenuService.updateMenu(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    // 功能：删除菜单
    public Result<Void> deleteMenu(@PathVariable Long id) {
        adminMenuService.deleteMenu(id);
        return Result.success();
    }
}
