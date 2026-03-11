package com.thinking.backendmall.service;

import com.thinking.backendmall.entity.Category;

import java.util.List;

public interface CategoryService {
    // 功能：查询可浏览分类列表（可按父级过滤）。
    List<Category> listCategories(Long parentId);
}
