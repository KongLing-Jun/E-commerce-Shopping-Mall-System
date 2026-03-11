package com.thinking.backendmall.service;

import com.thinking.backendmall.vo.CartItemView;

import java.util.List;

public interface CartService {
    // 功能：新增to购物车
    void addToCart(Long userId, Long productId, Integer quantity);

    // 功能：查询购物车明细
    List<CartItemView> listCartItems(Long userId);

    // 功能：更新购物车明细
    void updateCartItem(Long userId, Long cartItemId, Integer quantity, Integer checked);

    // 功能：删除购物车明细
    void deleteCartItem(Long userId, Long cartItemId);
}
