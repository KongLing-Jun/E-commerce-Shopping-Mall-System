package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.config.security.AuthContext;
import com.thinking.backendmall.service.MenuService;
import com.thinking.backendmall.vo.MenuTreeNode;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Result<List<MenuTreeNode>> listMyMenus() {
        String roleKey = AuthContext.getRoleKey();
        if (roleKey == null) {
            throw new BusinessException(401, "Unauthorized");
        }
        return Result.success(menuService.listMyMenus(roleKey));
    }
}
