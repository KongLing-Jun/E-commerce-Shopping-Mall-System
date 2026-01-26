package com.thinking.backendmall.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thinking.backendmall.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuRepository extends BaseMapper<Menu> {
    @Select("""
            SELECT m.perm_code
            FROM menu m
            INNER JOIN role_menu rm ON m.id = rm.menu_id
            WHERE rm.role_id = #{roleId}
              AND m.perm_code IS NOT NULL
              AND m.type = 'BUTTON'
            """)
    List<String> selectPermCodesByRoleId(Long roleId);

    @Select("""
            SELECT m.*
            FROM menu m
            INNER JOIN role_menu rm ON m.id = rm.menu_id
            WHERE rm.role_id = #{roleId}
              AND m.type = 'MENU'
              AND (m.visible IS NULL OR m.visible = 1)
            ORDER BY m.sort ASC, m.id ASC
            """)
    List<Menu> selectMenusByRoleId(Long roleId);
}
