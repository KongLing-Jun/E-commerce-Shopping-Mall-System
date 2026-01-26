package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinking.backendmall.entity.Banner;
import com.thinking.backendmall.entity.Category;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.repository.BannerRepository;
import com.thinking.backendmall.repository.CategoryRepository;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Banner> getBanners() {
        return bannerRepository.selectList(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort));
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort));
    }

    @Override
    public List<Product> getRecommendProducts() {
        Page<Product> page = new Page<>(1, 10);
        productRepository.selectPage(page, new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, "ON"));
        return page.getRecords();
    }
}
