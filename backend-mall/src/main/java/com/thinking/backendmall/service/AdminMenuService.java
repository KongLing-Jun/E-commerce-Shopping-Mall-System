package com.thinking.backendmall.service;

import com.thinking.backendmall.dto.MenuRequest;
import com.thinking.backendmall.vo.MenuTreeNode;

import java.util.List;

public interface AdminMenuService {
    List<MenuTreeNode> listMenuTree();

    void createMenu(MenuRequest request);

    void updateMenu(Long id, MenuRequest request);

    void deleteMenu(Long id);
}
