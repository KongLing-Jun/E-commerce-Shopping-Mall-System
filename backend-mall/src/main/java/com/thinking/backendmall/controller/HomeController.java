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
        List<Product> products = homeService.getRecommendProducts();
        Map<String, Object> result = new HashMap<>();
        result.put("categories", categories);
        result.put("products", products);
        return Result.success(result);
    }
}