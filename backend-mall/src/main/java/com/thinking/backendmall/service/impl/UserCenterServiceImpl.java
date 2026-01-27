package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.config.security.AuthContext;
import com.thinking.backendmall.entity.Order;
import com.thinking.backendmall.entity.Product;
import com.thinking.backendmall.entity.UserFavorite;
import com.thinking.backendmall.entity.UserFootprint;
import com.thinking.backendmall.repository.OrderRepository;
import com.thinking.backendmall.repository.ProductRepository;
import com.thinking.backendmall.repository.UserFavoriteRepository;
import com.thinking.backendmall.repository.UserFootprintRepository;
import com.thinking.backendmall.service.UserCenterService;
import com.thinking.backendmall.vo.UserFavoriteView;
import com.thinking.backendmall.vo.UserFootprintView;
import com.thinking.backendmall.vo.UserSummaryView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserCenterServiceImpl implements UserCenterService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    @Autowired
    private UserFootprintRepository userFootprintRepository;

    @Override
    public UserSummaryView getSummary() {
        Long userId = requireUserId();
        UserSummaryView summary = new UserSummaryView();
        summary.setTotalOrders(countOrders(userId, null));
        summary.setPendingOrders(countOrders(userId, 0));
        summary.setPaidOrders(countOrders(userId, 1));
        summary.setShippedOrders(countOrders(userId, 2));
        summary.setCompletedOrders(countOrders(userId, 3));
        summary.setFavoriteCount(countFavorites(userId));
        summary.setFootprintCount(countFootprints(userId));
        return summary;
    }

    @Override
    public List<UserFavoriteView> listFavorites() {
        Long userId = requireUserId();
        List<UserFavorite> favorites = userFavoriteRepository.selectList(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .orderByDesc(UserFavorite::getCreatedAt));
        return buildFavoriteViews(favorites);
    }

    @Override
    public void addFavorite(Long productId) {
        Long userId = requireUserId();
        if (productId == null) {
            throw new BusinessException(400, "ProductId is required");
        }
        Product product = productRepository.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "Product not found");
        }
        UserFavorite existing = userFavoriteRepository.selectOne(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getProductId, productId));
        if (existing != null) {
            return;
        }
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreatedAt(LocalDateTime.now());
        userFavoriteRepository.insert(favorite);
    }

    @Override
    public void removeFavorite(Long productId) {
        Long userId = requireUserId();
        if (productId == null) {
            return;
        }
        userFavoriteRepository.delete(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getProductId, productId));
    }

    @Override
    public List<UserFootprintView> listFootprints() {
        Long userId = requireUserId();
        List<UserFootprint> footprints = userFootprintRepository.selectList(new LambdaQueryWrapper<UserFootprint>()
                .eq(UserFootprint::getUserId, userId)
                .orderByDesc(UserFootprint::getViewedAt));
        return buildFootprintViews(footprints);
    }

    @Override
    public void removeFootprint(Long productId) {
        Long userId = requireUserId();
        if (productId == null) {
            return;
        }
        userFootprintRepository.delete(new LambdaQueryWrapper<UserFootprint>()
                .eq(UserFootprint::getUserId, userId)
                .eq(UserFootprint::getProductId, productId));
    }

    @Override
    public void recordFootprint(Long productId) {
        Long userId = AuthContext.getUserId();
        if (userId == null || productId == null) {
            return;
        }
        UserFootprint existing = userFootprintRepository.selectOne(new LambdaQueryWrapper<UserFootprint>()
                .eq(UserFootprint::getUserId, userId)
                .eq(UserFootprint::getProductId, productId));
        if (existing != null) {
            existing.setViewedAt(LocalDateTime.now());
            userFootprintRepository.updateById(existing);
            return;
        }
        UserFootprint footprint = new UserFootprint();
        footprint.setUserId(userId);
        footprint.setProductId(productId);
        footprint.setViewedAt(LocalDateTime.now());
        userFootprintRepository.insert(footprint);
    }

    private Long requireUserId() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "Unauthorized");
        }
        return userId;
    }

    private long countOrders(Long userId, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        Long count = orderRepository.selectCount(wrapper);
        return count == null ? 0 : count;
    }

    private long countFavorites(Long userId) {
        Long count = userFavoriteRepository.selectCount(new LambdaQueryWrapper<UserFavorite>()
                .eq(UserFavorite::getUserId, userId));
        return count == null ? 0 : count;
    }

    private long countFootprints(Long userId) {
        Long count = userFootprintRepository.selectCount(new LambdaQueryWrapper<UserFootprint>()
                .eq(UserFootprint::getUserId, userId));
        return count == null ? 0 : count;
    }

    private List<UserFavoriteView> buildFavoriteViews(List<UserFavorite> favorites) {
        List<UserFavoriteView> views = new ArrayList<>();
        if (favorites == null || favorites.isEmpty()) {
            return views;
        }
        Map<Long, Product> productMap = loadProductMap(favorites.stream().map(UserFavorite::getProductId).toList());
        for (UserFavorite favorite : favorites) {
            Product product = productMap.get(favorite.getProductId());
            if (product == null) {
                continue;
            }
            UserFavoriteView view = new UserFavoriteView();
            view.setProductId(product.getId());
            view.setName(product.getName());
            view.setBrief(product.getBrief());
            view.setPrice(product.getPrice());
            view.setStock(product.getStock());
            view.setStatus(product.getStatus());
            view.setCoverUrl(product.getCoverUrl());
            view.setFavoriteAt(favorite.getCreatedAt());
            views.add(view);
        }
        return views;
    }

    private List<UserFootprintView> buildFootprintViews(List<UserFootprint> footprints) {
        List<UserFootprintView> views = new ArrayList<>();
        if (footprints == null || footprints.isEmpty()) {
            return views;
        }
        Map<Long, Product> productMap = loadProductMap(footprints.stream().map(UserFootprint::getProductId).toList());
        for (UserFootprint footprint : footprints) {
            Product product = productMap.get(footprint.getProductId());
            if (product == null) {
                continue;
            }
            UserFootprintView view = new UserFootprintView();
            view.setProductId(product.getId());
            view.setName(product.getName());
            view.setBrief(product.getBrief());
            view.setPrice(product.getPrice());
            view.setStock(product.getStock());
            view.setStatus(product.getStatus());
            view.setCoverUrl(product.getCoverUrl());
            view.setViewedAt(footprint.getViewedAt());
            views.add(view);
        }
        return views;
    }

    private Map<Long, Product> loadProductMap(List<Long> productIds) {
        Map<Long, Product> map = new HashMap<>();
        if (productIds == null || productIds.isEmpty()) {
            return map;
        }
        for (Product product : productRepository.selectBatchIds(productIds)) {
            map.put(product.getId(), product);
        }
        return map;
    }
}
