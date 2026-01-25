package com.thinking.backendmall.service;

import com.thinking.backendmall.model.CartItem;
import com.thinking.backendmall.model.Product;
import com.thinking.backendmall.repository.InMemoryStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final InMemoryStore store;
    private final ProductService productService;

    public CartService(InMemoryStore store, ProductService productService) {
        this.store = store;
        this.productService = productService;
    }

    public List<CartItem> listCart(Long userId) {
        return new ArrayList<>(store.getOrCreateCart(userId));
    }

    public List<CartItem> addToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }
        Product product = productService.getProduct(productId);
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("库存不足");
        }
        List<CartItem> cart = store.getOrCreateCart(userId);
        Optional<CartItem> existing = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            cart.add(new CartItem(store.nextCartId(), userId, productId, quantity));
        }
        return new ArrayList<>(cart);
    }

    public List<CartItem> updateQuantity(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }
        List<CartItem> cart = store.getOrCreateCart(userId);
        cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        return new ArrayList<>(cart);
    }

    public List<CartItem> removeItem(Long userId, Long productId) {
        List<CartItem> cart = store.getOrCreateCart(userId);
        cart.removeIf(item -> item.getProductId().equals(productId));
        return new ArrayList<>(cart);
    }

    public void clearCart(Long userId) {
        store.getOrCreateCart(userId).clear();
    }
}
