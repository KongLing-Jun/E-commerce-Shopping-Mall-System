package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminCategoryRequest;
import com.thinking.backendmall.entity.Category;

public interface AdminCategoryService {
    // 功能：查询分类
    PageResult<Category> listCategories(String keyword, Integer status, int page, int size);

    // 功能：创建分类
    Category createCategory(AdminCategoryRequest request);

    // 功能：更新分类
    Category updateCategory(Long id, AdminCategoryRequest request);

    // 功能：更新状态
    void updateStatus(Long id, Integer status);

    // 功能：删除分类
    void deleteCategory(Long id);
}
