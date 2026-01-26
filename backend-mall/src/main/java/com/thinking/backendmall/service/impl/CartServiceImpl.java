package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.entity.CartItem;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.repository.CartItemRepository;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.service.CartService;
import com.thinking.backendmall.vo.CartItemView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public void addToCart(Long userId, Long productId, Integer quantity) {
        if (productId == null) {
            throw new BusinessException("ProductId is required");
        }
        int qty = quantity == null || quantity <= 0 ? 1 : quantity;
        Product product = productRepository.selectById(productId);
        if (product == null || !"ON".equals(product.getStatus())) {
            throw new BusinessException("Product not available");
        }

        CartItem existing = cartItemRepository.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId));

        int existingQty = existing == null ? 0 : existing.getQuantity();
        Integer stock = product.getStock();
        if (stock != null && existingQty + qty > stock) {
            throw new BusinessException("Insufficient stock");
        }

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + qty);
            cartItemRepository.updateById(existing);
            return;
        }

        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(productId);
        item.setQuantity(qty);
        item.setChecked(1);
        item.setCreatedAt(LocalDateTime.now());
        cartItemRepository.insert(item);
    }

    @Override
    public List<CartItemView> listCartItems(Long userId) {
        List<CartItem> items = cartItemRepository.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId));
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> productIds = new ArrayList<>();
        for (CartItem item : items) {
            productIds.add(item.getProductId());
        }
        List<Product> products = productRepository.selectBatchIds(productIds);
        Map<Long, Product> productMap = new HashMap<>();
        for (Product product : products) {
            productMap.put(product.getId(), product);
        }
        List<CartItemView> views = new ArrayList<>();
        for (CartItem item : items) {
            Product product = productMap.get(item.getProductId());
            if (product == null) {
                continue;
            }
            CartItemView view = new CartItemView();
            view.setCartItemId(item.getId());
            view.setProductId(item.getProductId());
            view.setName(product.getName());
            view.setPrice(product.getPrice() == null ? BigDecimal.ZERO : product.getPrice());
            view.setImage(product.getCoverUrl());
            view.setQuantity(item.getQuantity());
            view.setChecked(item.getChecked());
            views.add(view);
        }
        return views;
    }

    @Override
    @Transactional
    public void updateCartItem(Long userId, Long cartItemId, Integer quantity, Integer checked) {
        CartItem item = cartItemRepository.selectById(cartItemId);
        if (item == null || !userId.equals(item.getUserId())) {
            throw new BusinessException(404, "Cart item not found");
        }
        if (quantity != null) {
            if (quantity <= 0) {
                throw new BusinessException("Quantity must be positive");
            }
            Product product = productRepository.selectById(item.getProductId());
            if (product == null || !"ON".equals(product.getStatus())) {
                throw new BusinessException("Product not available");
            }
            Integer stock = product.getStock();
            if (stock != null && quantity > stock) {
                throw new BusinessException("Insufficient stock");
            }
            item.setQuantity(quantity);
        }
        if (checked != null) {
            item.setChecked(checked);
        }
        cartItemRepository.updateById(item);
    }

    @Override
    @Transactional
    public void deleteCartItem(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.selectById(cartItemId);
        if (item == null || !userId.equals(item.getUserId())) {
            throw new BusinessException(404, "Cart item not found");
        }
        cartItemRepository.deleteById(cartItemId);
    }
}
