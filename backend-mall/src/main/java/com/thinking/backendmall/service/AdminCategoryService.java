package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminCategoryRequest;
import com.thinking.backendmall.entity.Category;

public interface AdminCategoryService {
    PageResult<Category> listCategories(String keyword, Integer status, int page, int size);

    Category createCategory(AdminCategoryRequest request);

    Category updateCategory(Long id, AdminCategoryRequest request);

    void updateStatus(Long id, Integer status);

    void deleteCategory(Long id);
}
