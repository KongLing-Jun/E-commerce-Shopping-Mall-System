package com.thinking.backendmall.repository;

import com.thinking.backendmall.model.CartItem;
import com.thinking.backendmall.model.Order;
import com.thinking.backendmall.model.Product;
import com.thinking.backendmall.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStore {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final Map<Long, List<CartItem>> carts = new ConcurrentHashMap<>();
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final Map<String, Long> tokens = new ConcurrentHashMap<>();

    private final AtomicLong userIdGenerator = new AtomicLong(1);
    private final AtomicLong productIdGenerator = new AtomicLong(1000);
    private final AtomicLong cartIdGenerator = new AtomicLong(1);
    private final AtomicLong orderIdGenerator = new AtomicLong(1);
    private final AtomicLong orderItemIdGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        Product phone = new Product(productIdGenerator.getAndIncrement(),
                "星云X1 手机",
                "6.7英寸AMOLED屏幕，旗舰影像系统",
                "手机",
                new BigDecimal("3999.00"),
                50,
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=800&q=80");
        Product laptop = new Product(productIdGenerator.getAndIncrement(),
                "轻薄Pro 笔记本",
                "13英寸轻薄本，办公学习更高效",
                "电脑",
                new BigDecimal("5899.00"),
                30,
                "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=800&q=80");
        Product headset = new Product(productIdGenerator.getAndIncrement(),
                "霜语 降噪耳机",
                "主动降噪 + 40小时续航",
                "配件",
                new BigDecimal("699.00"),
                120,
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80");
        products.put(phone.getId(), phone);
        products.put(laptop.getId(), laptop);
        products.put(headset.getId(), headset);
    }

    public Map<Long, User> getUsers() {
        return users;
    }

    public Map<Long, Product> getProducts() {
        return products;
    }

    public Map<Long, List<CartItem>> getCarts() {
        return carts;
    }

    public Map<Long, Order> getOrders() {
        return orders;
    }

    public Map<String, Long> getTokens() {
        return tokens;
    }

    public long nextUserId() {
        return userIdGenerator.getAndIncrement();
    }

    public long nextProductId() {
        return productIdGenerator.getAndIncrement();
    }

    public long nextCartId() {
        return cartIdGenerator.getAndIncrement();
    }

    public long nextOrderId() {
        return orderIdGenerator.getAndIncrement();
    }

    public long nextOrderItemId() {
        return orderItemIdGenerator.getAndIncrement();
    }

    public List<CartItem> getOrCreateCart(Long userId) {
        return carts.computeIfAbsent(userId, id -> new ArrayList<>());
    }
}
