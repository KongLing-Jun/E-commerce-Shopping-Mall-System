package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinking.backendmall.common.CacheKeys;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.HtmlSanitizer;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminProductRequest;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.entity.ProductImage;
import com.thinking.backendmall.repository.ProductImageRepository;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.service.AdminProductService;
import com.thinking.backendmall.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class AdminProductServiceImpl implements AdminProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private CacheService cacheService;

    @Override
    public PageResult<Product> listProducts(String keyword, Long categoryId, String status, int page, int size) {
        Page<Product> pageResult = new Page<>(page + 1L, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Product::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.orderByDesc(Product::getCreatedAt);
        productRepository.selectPage(pageResult, wrapper);
        boolean last = pageResult.getCurrent() >= pageResult.getPages();
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), pageResult.getPages(), page, size, last);
    }

    @Override
    public Product createProduct(AdminProductRequest request) {
        Product product = new Product();
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setBrief(request.getBrief());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(normalizeStatus(request.getStatus(), "ON"));
        product.setCoverUrl(request.getCoverUrl());
        product.setDetailHtml(HtmlSanitizer.sanitize(request.getDetailHtml()));
        product.setCreatedAt(LocalDateTime.now());
        productRepository.insert(product);
        saveProductImages(product.getId(), request.getCoverUrl(), request.getImageUrls());
        evictProductCaches();
        return product;
    }

    @Override
    public Product updateProduct(Long id, AdminProductRequest request) {
        Product existing = productRepository.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "Product not found");
        }
        existing.setCategoryId(request.getCategoryId());
        existing.setName(request.getName());
        existing.setBrief(request.getBrief());
        existing.setPrice(request.getPrice());
        existing.setStock(request.getStock());
        existing.setStatus(normalizeStatus(request.getStatus(), existing.getStatus()));
        existing.setCoverUrl(request.getCoverUrl());
        existing.setDetailHtml(HtmlSanitizer.sanitize(request.getDetailHtml()));
        productRepository.updateById(existing);
        saveProductImages(existing.getId(), request.getCoverUrl(), request.getImageUrls());
        evictProductCaches();
        return existing;
    }

    @Override
    public void updateStatus(Long id, String status) {
        Product existing = productRepository.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "Product not found");
        }
        existing.setStatus(normalizeStatus(status, existing.getStatus()));
        productRepository.updateById(existing);
        evictProductCaches();
    }

    @Override
    public void deleteProduct(Long id) {
        Product existing = productRepository.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "Product not found");
        }
        productImageRepository.delete(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, id));
        productRepository.deleteById(id);
        evictProductCaches();
    }

    private void saveProductImages(Long productId, String coverUrl, List<String> imageUrls) {
        productImageRepository.delete(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId));

        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        if (coverUrl != null && !coverUrl.isBlank()) {
            normalized.add(coverUrl.trim());
        }
        if (imageUrls != null) {
            for (String imageUrl : imageUrls) {
                if (imageUrl != null && !imageUrl.isBlank()) {
                    normalized.add(imageUrl.trim());
                }
            }
        }

        List<String> finalImages = new ArrayList<>(normalized);
        int sort = 1;
        for (String imageUrl : finalImages) {
            ProductImage productImage = new ProductImage();
            productImage.setProductId(productId);
            productImage.setUrl(imageUrl);
            productImage.setSort(sort++);
            productImageRepository.insert(productImage);
        }
    }

    private String normalizeStatus(String status, String fallback) {
        if (status == null || status.isBlank()) {
            return fallback;
        }
        return status.trim().toUpperCase();
    }

    private void evictProductCaches() {
        cacheService.deleteByPrefix(CacheKeys.PRODUCT_DETAIL_PREFIX);
        cacheService.deleteByPrefix(CacheKeys.PRODUCT_SEARCH_PREFIX);
        cacheService.delete(CacheKeys.HOME_RECOMMEND);
        cacheService.delete(CacheKeys.HOME_HOT);
        cacheService.delete(CacheKeys.HOME_PROMO);
    }
}
