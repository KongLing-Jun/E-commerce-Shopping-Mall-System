package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.vo.OrderPreResponse;
import com.thinking.backendmall.vo.OrderView;

import java.math.BigDecimal;

public interface OrderService {
    OrderPreResponse preOrder(Long userId);

    String createOrder(Long userId, Long addressId);

    void payOrder(Long userId, String orderNo, BigDecimal payAmount);

    PageResult<OrderView> listOrders(Long userId, Integer status, int page, int size);

    void confirmOrder(Long userId, String orderNo);
}
