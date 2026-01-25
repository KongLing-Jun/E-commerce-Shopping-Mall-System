package com.thinking.backendmall.service;

import com.thinking.backendmall.model.Product;
import com.thinking.backendmall.repository.InMemoryStore;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final InMemoryStore store;

    public ProductService(InMemoryStore store) {
        this.store = store;
    }

    public List<Product> listProducts(String keyword) {
        return store.getProducts().values().stream()
                .filter(product -> keyword == null || keyword.isBlank()
                        || product.getName().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))
                        || product.getDescription().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT)))
                .sorted(Comparator.comparing(Product::getId))
                .collect(Collectors.toList());
    }

    public Product getProduct(Long id) {
        Product product = store.getProducts().get(id);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        return product;
    }
}
