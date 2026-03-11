package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("menu")
public class Menu {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long parentId;
    private String name;
    private String path;
    private String component;
    private String type; // MENU/BUTTON
    private String permCode;
    private Integer sort;
    private Integer visible; // 1显示 0隐藏
    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取parentid
    public Long getParentId() {
        return parentId;
    }

    // 功能：设置parentid
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    // 功能：获取name
    public String getName() {
        return name;
    }

    // 功能：设置name
    public void setName(String name) {
        this.name = name;
    }

    // 功能：获取path
    public String getPath() {
        return path;
    }

    // 功能：设置path
    public void setPath(String path) {
        this.path = path;
    }

    // 功能：获取component
    public String getComponent() {
        return component;
    }

    // 功能：设置component
    public void setComponent(String component) {
        this.component = component;
    }

    // 功能：获取type
    public String getType() {
        return type;
    }

    // 功能：设置type
    public void setType(String type) {
        this.type = type;
    }

    // 功能：获取权限code
    public String getPermCode() {
        return permCode;
    }

    // 功能：设置权限code
    public void setPermCode(String permCode) {
        this.permCode = permCode;
    }

    // 功能：获取排序
    public Integer getSort() {
        return sort;
    }

    // 功能：设置排序
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    // 功能：获取visible
    public Integer getVisible() {
        return visible;
    }

    // 功能：设置visible
    public void setVisible(Integer visible) {
        this.visible = visible;
    }
}
