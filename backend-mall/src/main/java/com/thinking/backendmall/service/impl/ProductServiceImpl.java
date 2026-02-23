package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.thinking.backendmall.common.CacheKeys;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.entity.ProductImage;
import com.thinking.backendmall.repository.ProductImageRepository;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.service.CacheService;
import com.thinking.backendmall.service.ProductService;
import com.thinking.backendmall.vo.ProductDetailView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private CacheService cacheService;

    @Value("${app.cache.product-list-ttl:180}")
    private long productListTtl;

    @Value("${app.cache.product-detail-ttl:300}")
    private long productDetailTtl;

    @Override
    public PageResult<Product> searchProducts(String keyword, Long categoryId, int page, int size) {
        String cacheKey = CacheKeys.productSearch(keyword, categoryId, page, size);
        PageResult<Product> cached = cacheService.get(cacheKey, new TypeReference<PageResult<Product>>() {});
        if (cached != null) {
            return cached;
        }
        Page<Product> pageResult = new Page<>(page + 1L, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, "ON");
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Product::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        productRepository.selectPage(pageResult, wrapper);
        long totalElements = pageResult.getTotal();
        long totalPages = pageResult.getPages();
        boolean last = pageResult.getCurrent() >= totalPages;
        PageResult<Product> result = new PageResult<>(pageResult.getRecords(), totalElements, totalPages, page, size, last);
        cacheService.set(cacheKey, result, Duration.ofSeconds(productListTtl));
        return result;
    }

    @Override
    public ProductDetailView getProductById(Long productId) {
        if (productId == null) {
            return null;
        }
        String cacheKey = CacheKeys.productDetail(productId);
        ProductDetailView cached = cacheService.get(cacheKey, ProductDetailView.class);
        if (cached != null) {
            return cached;
        }
        Product product = productRepository.selectById(productId);
        if (product == null) {
            return null;
        }
        ProductDetailView detailView = toDetailView(product);
        cacheService.set(cacheKey, detailView, Duration.ofSeconds(productDetailTtl));
        return detailView;
    }

    private ProductDetailView toDetailView(Product product) {
        ProductDetailView view = new ProductDetailView();
        view.setId(product.getId());
        view.setCategoryId(product.getCategoryId());
        view.setName(product.getName());
        view.setBrief(product.getBrief());
        view.setPrice(product.getPrice());
        view.setStock(product.getStock());
        view.setStatus(product.getStatus());
        view.setCoverUrl(product.getCoverUrl());
        view.setDetailHtml(product.getDetailHtml());
        view.setCreatedAt(product.getCreatedAt());
        view.setImages(loadImages(product));
        view.setSpecs(buildSpecs(product));
        return view;
    }

    private List<String> loadImages(Product product) {
        List<ProductImage> imageList = productImageRepository.selectList(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, product.getId())
                .orderByAsc(ProductImage::getSort)
                .orderByAsc(ProductImage::getId));
        List<String> images = new ArrayList<>();
        for (ProductImage image : imageList) {
            if (image.getUrl() != null && !image.getUrl().isBlank()) {
                images.add(image.getUrl());
            }
        }
        // Fallback to cover image when product_image has no rows.
        if (images.isEmpty() && product.getCoverUrl() != null && !product.getCoverUrl().isBlank()) {
            images.add(product.getCoverUrl());
        }
        return images;
    }

    private Map<String, String> buildSpecs(Product product) {
        Map<String, String> specs = new LinkedHashMap<>();
        specs.put("Stock", String.valueOf(product.getStock() == null ? 0 : product.getStock()));
        specs.put("Status", "ON".equals(product.getStatus()) ? "On Sale" : "Off Shelf");
        specs.put("Product Code", "P-" + product.getId());
        specs.put("Category Code", "C-" + product.getCategoryId());
        if (product.getName() != null) {
            String name = product.getName().toLowerCase();
            if (name.contains("phone")) {
                specs.put("Network", "5G");
                specs.put("Refresh Rate", "120Hz");
            } else if (name.contains("laptop")) {
                specs.put("Memory", "16GB");
                specs.put("Storage", "512GB SSD");
            } else if (name.contains("earbud") || name.contains("watch") || name.contains("headphone")) {
                specs.put("Connectivity", "Bluetooth 5.2");
                specs.put("Battery Life", "20 hours");
            }
        }
        return specs;
    }
}
