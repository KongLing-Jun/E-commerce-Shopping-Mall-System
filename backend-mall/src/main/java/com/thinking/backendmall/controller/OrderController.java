package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.config.security.AuthContext;
import com.thinking.backendmall.dto.OrderCreateRequest;
import com.thinking.backendmall.dto.OrderPayRequest;
import com.thinking.backendmall.vo.OrderPreResponse;
import com.thinking.backendmall.vo.OrderView;
import com.thinking.backendmall.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/pre")
    public Result<OrderPreResponse> preOrder() {
        return Result.success(orderService.preOrder(requireUserId()));
    }

    @PostMapping
    public Result<Map<String, Object>> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        String orderNo = orderService.createOrder(requireUserId(), request.getAddressId());
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", orderNo);
        return Result.success(result);
    }

    @PostMapping("/{orderNo}/pay")
    public Result<Void> payOrder(@PathVariable String orderNo,
                                 @Valid @RequestBody(required = false) OrderPayRequest request) {
        orderService.payOrder(requireUserId(), orderNo, request == null ? null : request.getPayAmount());
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<OrderView>> listOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(orderService.listOrders(requireUserId(), status, page, size));
    }

    @PostMapping("/{orderNo}/confirm")
    public Result<Void> confirmOrder(@PathVariable String orderNo) {
        orderService.confirmOrder(requireUserId(), orderNo);
        return Result.success();
    }

    private Long requireUserId() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "Unauthorized");
        }
        return userId;
    }
}
