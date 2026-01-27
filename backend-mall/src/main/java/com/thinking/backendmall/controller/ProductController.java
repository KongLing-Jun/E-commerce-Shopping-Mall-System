package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.service.ProductService;
import com.thinking.backendmall.service.UserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserCenterService userCenterService;

    @GetMapping
    public Result<PageResult<Product>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Product> products = productService.searchProducts(keyword, categoryId, page, size);
        return Result.success(products);
    }

    @GetMapping("/{productId}")
    public Result<Product> getProduct(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return Result.error(404, "Product not found");
        }
        userCenterService.recordFootprint(productId);
        return Result.success(product);
    }
}
