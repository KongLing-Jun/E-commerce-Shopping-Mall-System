package com.thinking.backendmall.controller;

import com.thinking.backendmall.dto.ApiResponse;
import com.thinking.backendmall.dto.OrderCreateRequest;
import com.thinking.backendmall.dto.OrderStatusUpdateRequest;
import com.thinking.backendmall.model.Order;
import com.thinking.backendmall.service.AuthService;
import com.thinking.backendmall.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final AuthService authService;
    private final OrderService orderService;

    public OrderController(AuthService authService, OrderService orderService) {
        this.authService = authService;
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<Order> create(@RequestHeader("X-Auth-Token") String token,
                                     @RequestBody OrderCreateRequest request) {
        Long userId = authService.resolveUserId(token);
        return ApiResponse.success(orderService.createOrder(userId, request.getAddress()));
    }

    @GetMapping
    public ApiResponse<List<Order>> list(@RequestHeader("X-Auth-Token") String token) {
        Long userId = authService.resolveUserId(token);
        return ApiResponse.success(orderService.listOrders(userId));
    }

    @PostMapping("/{orderId}/status")
    public ApiResponse<Order> updateStatus(@RequestHeader("X-Auth-Token") String token,
                                           @PathVariable Long orderId,
                                           @RequestBody OrderStatusUpdateRequest request) {
        Long userId = authService.resolveUserId(token);
        return ApiResponse.success(orderService.updateStatus(userId, orderId, request.getStatus()));
    }
}
