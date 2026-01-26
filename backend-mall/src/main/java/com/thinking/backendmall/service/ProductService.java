package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.entity.Product;

public interface ProductService {
    PageResult<Product> searchProducts(String keyword, Long categoryId, int page, int size);

    Product getProductById(Long productId);
}
