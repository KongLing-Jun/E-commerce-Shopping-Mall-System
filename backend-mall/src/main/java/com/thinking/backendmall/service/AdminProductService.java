package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminProductRequest;
import com.thinking.backendmall.entity.Product;

public interface AdminProductService {
    PageResult<Product> listProducts(String keyword, Long categoryId, String status, int page, int size);

    Product createProduct(AdminProductRequest request);

    Product updateProduct(Long id, AdminProductRequest request);

    void updateStatus(Long id, String status);

    void deleteProduct(Long id);
}
