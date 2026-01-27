package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.thinking.backendmall.common.CacheKeys;
import com.thinking.backendmall.entity.Banner;
import com.thinking.backendmall.entity.Category;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.repository.BannerRepository;
import com.thinking.backendmall.repository.CategoryRepository;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.service.CacheService;
import com.thinking.backendmall.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheService cacheService;

    @Value("${app.cache.home-ttl:180}")
    private long homeTtl;

    @Override
    public List<Banner> getBanners() {
        List<Banner> cached = cacheService.get(CacheKeys.HOME_BANNERS, new TypeReference<List<Banner>>() {});
        if (cached != null) {
            return cached;
        }
        List<Banner> banners = bannerRepository.selectList(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort));
        cacheService.set(CacheKeys.HOME_BANNERS, banners, Duration.ofSeconds(homeTtl));
        return banners;
    }

    @Override
    public List<Category> getCategories() {
        List<Category> cached = cacheService.get(CacheKeys.HOME_CATEGORIES, new TypeReference<List<Category>>() {});
        if (cached != null) {
            return cached;
        }
        List<Category> categories = categoryRepository.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort));
        cacheService.set(CacheKeys.HOME_CATEGORIES, categories, Duration.ofSeconds(homeTtl));
        return categories;
    }

    @Override
    public List<Product> getRecommendProducts() {
        List<Product> cached = cacheService.get(CacheKeys.HOME_RECOMMEND, new TypeReference<List<Product>>() {});
        if (cached != null) {
            return cached;
        }
        Page<Product> page = new Page<>(1, 10);
        productRepository.selectPage(page, new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, "ON"));
        List<Product> products = page.getRecords();
        cacheService.set(CacheKeys.HOME_RECOMMEND, products, Duration.ofSeconds(homeTtl));
        return products;
    }
}
