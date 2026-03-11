package com.thinking.backendmall.service;

import com.thinking.backendmall.vo.MenuTreeNode;

import java.util.List;

public interface MenuService {
    // 功能：查询我的菜单
    List<MenuTreeNode> listMyMenus(String roleKey);

    // 功能：查询我的权限
    List<String> listMyPerms(String roleKey);
}
