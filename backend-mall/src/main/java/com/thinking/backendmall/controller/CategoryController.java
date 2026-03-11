package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.entity.Category;
import com.thinking.backendmall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    // 功能：提供前台分类浏览数据（支持按父级筛选）。
    public Result<List<Category>> listCategories(@RequestParam(required = false) Long parentId) {
        return Result.success(categoryService.listCategories(parentId));
    }
}
