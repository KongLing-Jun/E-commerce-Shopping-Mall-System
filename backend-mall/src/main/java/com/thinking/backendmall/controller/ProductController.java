package com.thinking.backendmall.controller;

import com.thinking.backendmall.dto.ApiResponse;
import com.thinking.backendmall.model.Product;
import com.thinking.backendmall.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<Product>> list(@RequestParam(value = "keyword", required = false) String keyword) {
        return ApiResponse.success(productService.listProducts(keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> detail(@PathVariable Long id) {
        return ApiResponse.success(productService.getProduct(id));
    }
}
