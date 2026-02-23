package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.entity.Banner;
import com.thinking.backendmall.entity.Category;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/banners")
    public Result<List<Banner>> getBanners() {
        List<Banner> banners = homeService.getBanners();
        return Result.success(banners);
    }

    @GetMapping("/recommend")
    public Result<Map<String, Object>> getRecommend() {
        List<Category> categories = homeService.getCategories();
        List<Product> recommendProducts = homeService.getRecommendProducts();
        List<Product> hotProducts = homeService.getHotProducts();
        List<Product> promoProducts = homeService.getPromoProducts();
        Map<String, Object> result = new HashMap<>();
        result.put("categories", categories);
        // Keep "products" for backward compatibility with existing frontend.
        result.put("products", recommendProducts);
        result.put("recommendProducts", recommendProducts);
        result.put("hotProducts", hotProducts);
        result.put("promoProducts", promoProducts);
        return Result.success(result);
    }

    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts() {
        return Result.success(homeService.getHotProducts());
    }

    @GetMapping("/promo")
    public Result<List<Product>> getPromoProducts() {
        return Result.success(homeService.getPromoProducts());
    }
}
