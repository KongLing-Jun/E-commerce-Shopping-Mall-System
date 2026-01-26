package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.entity.CartItem;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.entity.User;
import com.thinking.backendmall.repository.CartItemRepository;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.repository.UserRepository;
import com.thinking.backendmall.service.AdminCartService;
import com.thinking.backendmall.vo.AdminCartItemView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminCartServiceImpl implements AdminCartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public PageResult<AdminCartItemView> listCartItems(Long userId, int page, int size) {
        Page<CartItem> pageResult = new Page<>(page + 1L, size);
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(CartItem::getUserId, userId);
        }
        wrapper.orderByDesc(CartItem::getCreatedAt);
        cartItemRepository.selectPage(pageResult, wrapper);

        List<CartItem> items = pageResult.getRecords();
        List<Long> userIds = new ArrayList<>();
        List<Long> productIds = new ArrayList<>();
        for (CartItem item : items) {
            userIds.add(item.getUserId());
            productIds.add(item.getProductId());
        }

        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            for (User user : userRepository.selectBatchIds(userIds)) {
                userMap.put(user.getId(), user);
            }
        }

        Map<Long, Product> productMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            for (Product product : productRepository.selectBatchIds(productIds)) {
                productMap.put(product.getId(), product);
            }
        }

        List<AdminCartItemView> views = new ArrayList<>();
        for (CartItem item : items) {
            Product product = productMap.get(item.getProductId());
            User user = userMap.get(item.getUserId());
            AdminCartItemView view = new AdminCartItemView();
            view.setCartItemId(item.getId());
            view.setUserId(item.getUserId());
            view.setUsername(user == null ? null : user.getUsername());
            view.setProductId(item.getProductId());
            view.setProductName(product == null ? null : product.getName());
            view.setPrice(product == null ? null : product.getPrice());
            view.setQuantity(item.getQuantity());
            view.setChecked(item.getChecked());
            view.setCreatedAt(item.getCreatedAt());
            views.add(view);
        }

        boolean last = pageResult.getCurrent() >= pageResult.getPages();
        return new PageResult<>(views, pageResult.getTotal(), pageResult.getPages(), page, size, last);
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        CartItem item = cartItemRepository.selectById(cartItemId);
        if (item == null) {
            throw new BusinessException(404, "Cart item not found");
        }
        cartItemRepository.deleteById(cartItemId);
    }
}
