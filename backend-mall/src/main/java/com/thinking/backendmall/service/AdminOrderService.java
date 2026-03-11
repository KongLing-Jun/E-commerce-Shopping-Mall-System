package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.dto.AdminOrderTrackingRequest;
import com.thinking.backendmall.vo.AdminOrderView;

public interface AdminOrderService {
    // 功能：分页查询订单列表。
    PageResult<AdminOrderView> listOrders(String orderNo, Long userId, Integer status, int page, int size);

    // 功能：发货订单并记录物流信息。
    void shipOrder(String orderNo, String expressNo, String expressCompany);

    // 功能：导出订单。
    byte[] exportOrders(String orderNo, Long userId, Integer status);

    // 功能：新增订单物流轨迹记录。
    void addTrackingEvent(String orderNo, AdminOrderTrackingRequest request);
}
