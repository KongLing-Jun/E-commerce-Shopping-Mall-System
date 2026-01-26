package com.thinking.backendmall.service;

import com.thinking.backendmall.entity.Banner;
import com.thinking.backendmall.entity.Category;
import com.thinking.backendmall.entity.Product;

import java.util.List;

public interface HomeService {
    List<Banner> getBanners();

    List<Category> getCategories();

    List<Product> getRecommendProducts();
}
