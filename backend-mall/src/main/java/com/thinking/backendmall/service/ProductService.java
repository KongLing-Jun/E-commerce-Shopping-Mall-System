package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.vo.ProductDetailView;

public interface ProductService {
    PageResult<Product> searchProducts(String keyword, Long categoryId, int page, int size);

    ProductDetailView getProductById(Long productId);
}
