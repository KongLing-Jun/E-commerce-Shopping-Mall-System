package com.thinking.backendmall.service;

import com.thinking.backendmall.dto.MenuRequest;
import com.thinking.backendmall.vo.MenuTreeNode;

import java.util.List;

public interface AdminMenuService {
    // 功能：查询菜单tree
    List<MenuTreeNode> listMenuTree();

    // 功能：创建菜单
    void createMenu(MenuRequest request);

    // 功能：更新菜单
    void updateMenu(Long id, MenuRequest request);

    // 功能：删除菜单
    void deleteMenu(Long id);
}
