package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("category")
public class Category {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;
    private Long parentId;
    private Integer sort;
    private Integer status; // 1启用 0禁用

    // 功能：获取id
    public Long getId() {
        return id;
    }

    // 功能：设置id
    public void setId(Long id) {
        this.id = id;
    }

    // 功能：获取状态
    public Integer getStatus() {
        return status;
    }

    // 功能：设置状态
    public void setStatus(Integer status) {
        this.status = status;
    }

    // 功能：获取排序
    public Integer getSort() {
        return sort;
    }

    // 功能：设置排序
    public void setSort(Integer sort) {
        this.sort = sort;
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
}
