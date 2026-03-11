package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.vo.ProductDetailView;

public interface ProductService {
    // 功能：处理搜索商品
    PageResult<Product> searchProducts(String keyword, Long categoryId, int page, int size);

    // 功能：获取商品byid
    ProductDetailView getProductById(Long productId);
}
