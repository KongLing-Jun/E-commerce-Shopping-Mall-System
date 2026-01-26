package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.entity.Menu;
import com.thinking.backendmall.entity.Role;
import com.thinking.backendmall.repository.MenuRepository;
import com.thinking.backendmall.repository.RoleRepository;
import com.thinking.backendmall.service.MenuService;
import com.thinking.backendmall.vo.MenuTreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<MenuTreeNode> listMyMenus(String roleKey) {
        if (roleKey == null || roleKey.isBlank()) {
            throw new BusinessException("Role is required");
        }
        Role role = roleRepository.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, roleKey));
        if (role == null) {
            return new ArrayList<>();
        }
        List<Menu> menus = menuRepository.selectMenusByRoleId(role.getId());
        return buildTree(menus);
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
