package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.ErrorCode;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.config.security.AuthContext;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.service.ProductService;
import com.thinking.backendmall.service.UserCenterService;
import com.thinking.backendmall.vo.ProductDetailView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        // Product list query with keyword/category pagination.
        PageResult<Product> products = productService.searchProducts(keyword, categoryId, page, size);
        return Result.success(products);
    }

    @GetMapping("/{productId}")
    public Result<ProductDetailView> getProduct(@PathVariable Long productId) {
        // 401 for unauthenticated access.
        if (AuthContext.getUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 404 when product does not exist.
        ProductDetailView productDetail = productService.getProductById(productId);
        if (productDetail == null) {
            throw new BusinessException(404, "Product not found");
        }

        // 410 when product is off shelf.
        if (!"ON".equals(productDetail.getStatus())) {
            throw new BusinessException(ErrorCode.PRODUCT_OFF_SHELF.getCode(), "Product is off shelf");
        }

        // Record footprint for user-center browsing history.
        userCenterService.recordFootprint(productId);
        return Result.success(productDetail);
    }
}
