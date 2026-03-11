package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.ErrorCode;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.config.security.AuthContext;
import com.thinking.backendmall.dto.OrderCreateRequest;
import com.thinking.backendmall.dto.OrderPayRequest;
import com.thinking.backendmall.service.OrderService;
import com.thinking.backendmall.vo.OrderInvoiceView;
import com.thinking.backendmall.vo.OrderPreResponse;
import com.thinking.backendmall.vo.OrderTrackingEventView;
import com.thinking.backendmall.vo.OrderView;
import com.thinking.backendmall.vo.RebuyResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/pre")
    // 功能：获取订单预览数据。
    public Result<OrderPreResponse> preOrder() {
        return Result.success(orderService.preOrder(requireUserId()));
    }

    @PostMapping
    // 功能：创建订单并扣减库存。
    public Result<Map<String, Object>> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        String orderNo = orderService.createOrder(requireUserId(), request.getAddressId());
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", orderNo);
        return Result.success(result);
    }

    @PostMapping("/{orderNo}/pay")
    // 功能：执行订单支付。
    public Result<Void> payOrder(@PathVariable String orderNo,
                                 @Valid @RequestBody(required = false) OrderPayRequest request) {
        orderService.payOrder(requireUserId(), orderNo, request == null ? null : request.getPayAmount());
        return Result.success();
    }

    @GetMapping("/{orderNo}")
    // 功能：获取订单详情。
    public Result<OrderView> getOrder(@PathVariable String orderNo) {
        return Result.success(orderService.getOrder(requireUserId(), orderNo));
    }

    @GetMapping("/{orderNo}/tracking")
    // 功能：获取订单物流轨迹记录。
    public Result<List<OrderTrackingEventView>> listTracking(@PathVariable String orderNo) {
        return Result.success(orderService.listTrackingEvents(requireUserId(), orderNo));
    }

    @GetMapping
    // 功能：分页查询订单列表。
    public Result<PageResult<OrderView>> listOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(orderService.listOrders(requireUserId(), status, page, size));
    }

    @PostMapping("/{orderNo}/confirm")
    // 功能：确认收货并更新订单状态。
    public Result<Void> confirmOrder(@PathVariable String orderNo) {
        orderService.confirmOrder(requireUserId(), orderNo);
        return Result.success();
    }

    @GetMapping("/{orderNo}/invoice")
    // 功能：获取订单发票。
    public Result<OrderInvoiceView> getInvoice(@PathVariable String orderNo) {
        return Result.success(orderService.getInvoice(requireUserId(), orderNo));
    }

    @PostMapping("/{orderNo}/rebuy")
    // 功能：再次购买并加入购物车。
    public Result<RebuyResponse> rebuy(@PathVariable String orderNo) {
        return Result.success(orderService.rebuy(requireUserId(), orderNo));
    }

    // 功能：获取并校验当前用户ID。
    private Long requireUserId() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return userId;
    }
}
