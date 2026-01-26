package com.thinking.backendmall.service;

import com.thinking.backendmall.vo.CartItemView;

import java.util.List;

public interface CartService {
    void addToCart(Long userId, Long productId, Integer quantity);

    List<CartItemView> listCartItems(Long userId);

    void updateCartItem(Long userId, Long cartItemId, Integer quantity, Integer checked);

    void deleteCartItem(Long userId, Long cartItemId);
}
