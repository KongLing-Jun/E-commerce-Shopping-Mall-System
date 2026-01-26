package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public PageResult<Product> searchProducts(String keyword, Long categoryId, int page, int size) {
        Page<Product> pageResult = new Page<>(page + 1L, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, "ON");
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Product::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        productRepository.selectPage(pageResult, wrapper);
        long totalElements = pageResult.getTotal();
        long totalPages = pageResult.getPages();
        boolean last = pageResult.getCurrent() >= totalPages;
        return new PageResult<>(pageResult.getRecords(), totalElements, totalPages, page, size, last);
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.selectById(productId);
    }
}
