package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.vo.OrderInvoiceView;
import com.thinking.backendmall.vo.OrderPreResponse;
import com.thinking.backendmall.vo.OrderTrackingEventView;
import com.thinking.backendmall.vo.OrderView;
import com.thinking.backendmall.vo.RebuyResponse;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    // 功能：获取订单预览数据。
    OrderPreResponse preOrder(Long userId);

    // 功能：创建订单并扣减库存。
    String createOrder(Long userId, Long addressId);

    // 功能：执行订单支付。
    void payOrder(Long userId, String orderNo, BigDecimal payAmount);

    // 功能：获取订单详情。
    OrderView getOrder(Long userId, String orderNo);

    // 功能：分页查询订单列表。
    PageResult<OrderView> listOrders(Long userId, Integer status, int page, int size);

    // 功能：确认收货并更新订单状态。
    void confirmOrder(Long userId, String orderNo);

    // 功能：获取订单发票。
    OrderInvoiceView getInvoice(Long userId, String orderNo);

    // 功能：再次购买并加入购物车。
    RebuyResponse rebuy(Long userId, String orderNo);

    // 功能：获取订单物流轨迹记录。
    List<OrderTrackingEventView> listTrackingEvents(Long userId, String orderNo);
}
