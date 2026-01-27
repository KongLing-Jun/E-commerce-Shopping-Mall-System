package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.thinking.backendmall.common.CacheKeys;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.service.CacheService;
import com.thinking.backendmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheService cacheService;

    @Value("${app.cache.product-list-ttl:180}")
    private long productListTtl;

    @Value("${app.cache.product-detail-ttl:300}")
    private long productDetailTtl;

    @Override
    public PageResult<Product> searchProducts(String keyword, Long categoryId, int page, int size) {
        String cacheKey = CacheKeys.productSearch(keyword, categoryId, page, size);
        PageResult<Product> cached = cacheService.get(cacheKey, new TypeReference<PageResult<Product>>() {});
        if (cached != null) {
            return cached;
        }
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
        PageResult<Product> result = new PageResult<>(pageResult.getRecords(), totalElements, totalPages, page, size, last);
        cacheService.set(cacheKey, result, Duration.ofSeconds(productListTtl));
        return result;
    }

    @Override
    public Product getProductById(Long productId) {
        if (productId == null) {
            return null;
        }
        String cacheKey = CacheKeys.productDetail(productId);
        Product cached = cacheService.get(cacheKey, Product.class);
        if (cached != null) {
            return cached;
        }
        Product product = productRepository.selectById(productId);
        if (product != null) {
            cacheService.set(cacheKey, product, Duration.ofSeconds(productDetailTtl));
        }
        return product;
    }
}
