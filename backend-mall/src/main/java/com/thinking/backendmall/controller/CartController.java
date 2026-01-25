package com.thinking.backendmall.controller;

import com.thinking.backendmall.dto.ApiResponse;
import com.thinking.backendmall.dto.CartUpdateRequest;
import com.thinking.backendmall.model.CartItem;
import com.thinking.backendmall.service.AuthService;
import com.thinking.backendmall.service.CartService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final AuthService authService;
    private final CartService cartService;

    public CartController(AuthService authService, CartService cartService) {
        this.authService = authService;
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<List<CartItem>> list(@RequestHeader("X-Auth-Token") String token) {
        Long userId = authService.resolveUserId(token);
        return ApiResponse.success(cartService.listCart(userId));
    }

    @PostMapping
    public ApiResponse<List<CartItem>> add(@RequestHeader("X-Auth-Token") String token,
                                           @RequestBody CartUpdateRequest request) {
        Long userId = authService.resolveUserId(token);
        return ApiResponse.success(cartService.addToCart(userId, request.getProductId(), request.getQuantity()));
    }

    @PostMapping("/update")
    public ApiResponse<List<CartItem>> update(@RequestHeader("X-Auth-Token") String token,
                                              @RequestBody CartUpdateRequest request) {
        Long userId = authService.resolveUserId(token);
        return ApiResponse.success(cartService.updateQuantity(userId, request.getProductId(), request.getQuantity()));
    }

    @DeleteMapping
    public ApiResponse<List<CartItem>> remove(@RequestHeader("X-Auth-Token") String token,
                                              @RequestParam Long productId) {
        Long userId = authService.resolveUserId(token);
        return ApiResponse.success(cartService.removeItem(userId, productId));
    }
}
