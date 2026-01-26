package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.dto.MenuRequest;
import com.thinking.backendmall.entity.Menu;
import com.thinking.backendmall.entity.RoleMenu;
import com.thinking.backendmall.repository.MenuRepository;
import com.thinking.backendmall.repository.RoleMenuRepository;
import com.thinking.backendmall.service.AdminMenuService;
import com.thinking.backendmall.vo.MenuTreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminMenuServiceImpl implements AdminMenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RoleMenuRepository roleMenuRepository;

    @Override
    public List<MenuTreeNode> listMenuTree() {
        List<Menu> menus = menuRepository.selectList(new LambdaQueryWrapper<Menu>()
                .orderByAsc(Menu::getSort)
                .orderByAsc(Menu::getId));
        return buildTree(menus);
    }

    @Override
    public void createMenu(MenuRequest request) {
        Menu menu = new Menu();
        menu.setParentId(request.getParentId());
        menu.setName(request.getName());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setType(request.getType());
        menu.setPermCode(request.getPermCode());
        menu.setSort(request.getSort() == null ? 0 : request.getSort());
        menu.setVisible(request.getVisible() == null ? 1 : request.getVisible());
        menuRepository.insert(menu);
    }

    @Override
    public void updateMenu(Long id, MenuRequest request) {
        Menu menu = menuRepository.selectById(id);
        if (menu == null) {
            throw new BusinessException(404, "Menu not found");
        }
        menu.setParentId(request.getParentId());
        menu.setName(request.getName());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setType(request.getType());
        menu.setPermCode(request.getPermCode());
        menu.setSort(request.getSort() == null ? menu.getSort() : request.getSort());
        menu.setVisible(request.getVisible() == null ? menu.getVisible() : request.getVisible());
        menuRepository.updateById(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        Menu menu = menuRepository.selectById(id);
        if (menu == null) {
            throw new BusinessException(404, "Menu not found");
        }
        roleMenuRepository.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getMenuId, id));
        menuRepository.deleteById(id);
    }

    private List<MenuTreeNode> buildTree(List<Menu> menus) {
        Map<Long, MenuTreeNode> nodeMap = new HashMap<>();
        List<MenuTreeNode> roots = new ArrayList<>();
        for (Menu menu : menus) {
            MenuTreeNode node = toNode(menu);
            nodeMap.put(node.getId(), node);
        }
        for (Menu menu : menus) {
            MenuTreeNode node = nodeMap.get(menu.getId());
            Long parentId = menu.getParentId();
            if (parentId == null || parentId == 0L || !nodeMap.containsKey(parentId)) {
                roots.add(node);
            } else {
                nodeMap.get(parentId).getChildren().add(node);
            }
        }
        return roots;
    }

    private MenuTreeNode toNode(Menu menu) {
        MenuTreeNode node = new MenuTreeNode();
        node.setId(menu.getId());
        node.setParentId(menu.getParentId());
        node.setName(menu.getName());
        node.setPath(menu.getPath());
        node.setComponent(menu.getComponent());
        node.setType(menu.getType());
        node.setPermCode(menu.getPermCode());
        node.setSort(menu.getSort());
        node.setVisible(menu.getVisible());
        return node;
    }
}
