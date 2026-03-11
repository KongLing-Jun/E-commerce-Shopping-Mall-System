package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminProductRequest;
import com.thinking.backendmall.entity.Product;

public interface AdminProductService {
    // 功能：查询商品
    PageResult<Product> listProducts(String keyword, Long categoryId, String status, int page, int size);

    // 功能：创建商品
    Product createProduct(AdminProductRequest request);

    // 功能：更新商品
    Product updateProduct(Long id, AdminProductRequest request);

    // 功能：更新状态
    void updateStatus(Long id, String status);

    // 功能：删除商品
    void deleteProduct(Long id);
}
