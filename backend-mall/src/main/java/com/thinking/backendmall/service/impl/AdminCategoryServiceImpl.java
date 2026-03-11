package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinking.backendmall.common.CacheKeys;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminCategoryRequest;
import com.thinking.backendmall.entity.Category;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.repository.CategoryRepository;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.service.AdminCategoryService;
import com.thinking.backendmall.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheService cacheService;

    @Override
    // 功能：查询分类
    public PageResult<Category> listCategories(String keyword, Integer status, int page, int size) {
        Page<Category> pageResult = new Page<>(page + 1L, size);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Category::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(Category::getStatus, status);
        }
        wrapper.orderByAsc(Category::getSort).orderByAsc(Category::getId);
        categoryRepository.selectPage(pageResult, wrapper);
        boolean last = pageResult.getCurrent() >= pageResult.getPages();
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), pageResult.getPages(), page, size, last);
    }

    @Override
    // 功能：创建分类
    public Category createCategory(AdminCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(defaultParent(request.getParentId()));
        category.setSort(defaultSort(request.getSort()));
        category.setStatus(resolveStatus(request.getStatus(), 1));
        categoryRepository.insert(category);
        cacheService.delete(CacheKeys.HOME_CATEGORIES);
        return category;
    }

    @Override
    // 功能：更新分类
    public Category updateCategory(Long id, AdminCategoryRequest request) {
        Category category = categoryRepository.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "Category not found");
        }
        category.setName(request.getName());
        category.setParentId(defaultParent(request.getParentId()));
        category.setSort(defaultSort(request.getSort()));
        category.setStatus(resolveStatus(request.getStatus(), category.getStatus()));
        categoryRepository.updateById(category);
        cacheService.delete(CacheKeys.HOME_CATEGORIES);
        return category;
    }

    @Override
    // 功能：更新状态
    public void updateStatus(Long id, Integer status) {
        Category category = categoryRepository.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "Category not found");
        }
        category.setStatus(resolveStatus(status, category.getStatus()));
        categoryRepository.updateById(category);
        cacheService.delete(CacheKeys.HOME_CATEGORIES);
    }

    @Override
    // 功能：删除分类
    public void deleteCategory(Long id) {
        Category category = categoryRepository.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "Category not found");
        }
        Long productCount = productRepository.selectCount(new LambdaQueryWrapper<Product>()
                .eq(Product::getCategoryId, id));
        if (productCount != null && productCount > 0) {
            throw new BusinessException(400, "Category contains products");
        }
        categoryRepository.deleteById(id);
        cacheService.delete(CacheKeys.HOME_CATEGORIES);
    }

    // 功能：处理defaultparent
    private Long defaultParent(Long parentId) {
        return parentId == null ? 0L : parentId;
    }

    // 功能：处理default排序
    private int defaultSort(Integer sort) {
        return sort == null ? 0 : sort;
    }

    // 功能：处理resolve状态
    private int resolveStatus(Integer status, Integer fallback) {
        if (status != null) {
            return status;
        }
        return fallback == null ? 1 : fallback;
    }
}
