package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.vo.AdminOrderView;

public interface AdminOrderService {
    PageResult<AdminOrderView> listOrders(String orderNo, Long userId, Integer status, int page, int size);

    void shipOrder(String orderNo, String expressNo, String expressCompany);

    byte[] exportOrders(String orderNo, Long userId, Integer status);
}
