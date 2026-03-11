package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thinking.backendmall.entity.Category;
import com.thinking.backendmall.repository.CategoryRepository;
import com.thinking.backendmall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    // 功能：按状态与父级过滤查询分类并按排序返回。
    public List<Category> listCategories(Long parentId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getId);
        if (parentId != null) {
            wrapper.eq(Category::getParentId, parentId);
        }
        return categoryRepository.selectList(wrapper);
    }
}
