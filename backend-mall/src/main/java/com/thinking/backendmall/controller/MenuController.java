package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.ErrorCode;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.config.security.AuthContext;
import com.thinking.backendmall.service.MenuService;
import com.thinking.backendmall.vo.MenuTreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    // 功能：查询我的菜单
    public Result<List<MenuTreeNode>> listMyMenus() {
        return Result.success(menuService.listMyMenus(requireRoleKey()));
    }

    @GetMapping("/perms")
    @PreAuthorize("isAuthenticated()")
    // 功能：查询我的权限
    public Result<List<String>> listMyPerms() {
        return Result.success(menuService.listMyPerms(requireRoleKey()));
    }

    // 功能：获取并校验当前角色标识
    private String requireRoleKey() {
        String roleKey = AuthContext.getRoleKey();
        if (roleKey == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return roleKey;
    }
}
