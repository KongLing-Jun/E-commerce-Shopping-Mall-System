package com.thinking.backendmall.service;

import com.thinking.backendmall.entity.Banner;
import com.thinking.backendmall.entity.Category;
import com.thinking.backendmall.entity.Product;

import java.util.List;

public interface HomeService {
    // 功能：获取轮播图
    List<Banner> getBanners();

    // 功能：获取分类
    List<Category> getCategories();

    // 功能：获取推荐商品
    List<Product> getRecommendProducts();

    // 功能：获取热销商品
    List<Product> getHotProducts();

    // 功能：获取促销商品
    List<Product> getPromoProducts();
}
