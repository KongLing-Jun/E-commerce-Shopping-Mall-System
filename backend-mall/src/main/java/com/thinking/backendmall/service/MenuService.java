package com.thinking.backendmall.service;

import com.thinking.backendmall.vo.MenuTreeNode;

import java.util.List;

public interface MenuService {
    List<MenuTreeNode> listMyMenus(String roleKey);
}
