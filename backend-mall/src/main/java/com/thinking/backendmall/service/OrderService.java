package com.thinking.backendmall.service;

import com.thinking.backendmall.model.CartItem;
import com.thinking.backendmall.model.Order;
import com.thinking.backendmall.model.OrderItem;
import com.thinking.backendmall.model.OrderStatus;
import com.thinking.backendmall.model.Product;
import com.thinking.backendmall.repository.InMemoryStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final InMemoryStore store;
    private final ProductService productService;
    private final CartService cartService;

    public OrderService(InMemoryStore store, ProductService productService, CartService cartService) {
        this.store = store;
        this.productService = productService;
        this.cartService = cartService;
    }

    public Order createOrder(Long userId, String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("请填写收货地址");
        }
        List<CartItem> cartItems = cartService.listCart(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("购物车为空");
        }
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            Product product = productService.getProduct(item.getProductId());
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("商品库存不足: " + product.getName());
            }
            product.setStock(product.getStock() - item.getQuantity());
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(lineTotal);
            OrderItem orderItem = new OrderItem(store.nextOrderItemId(), null,
                    product.getId(), product.getName(), product.getPrice(), item.getQuantity(), product.getImageUrl());
            orderItems.add(orderItem);
        }
        Order order = new Order(store.nextOrderId(), userId, address, total, OrderStatus.PENDING_PAYMENT, LocalDateTime.now());
        orderItems.forEach(orderItem -> orderItem.setOrderId(order.getId()));
        order.setItems(orderItems);
        store.getOrders().put(order.getId(), order);
        cartService.clearCart(userId);
        return order;
    }

    public List<Order> listOrders(Long userId) {
        return store.getOrders().values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public Order updateStatus(Long userId, Long orderId, OrderStatus status) {
        Order order = store.getOrders().get(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在");
        }
        order.setStatus(status);
        return order;
    }
}
