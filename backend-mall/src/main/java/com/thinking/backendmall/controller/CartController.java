package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.config.security.AuthContext;
import com.thinking.backendmall.dto.CartAddRequest;
import com.thinking.backendmall.vo.CartItemView;
import com.thinking.backendmall.dto.CartUpdateRequest;
import com.thinking.backendmall.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/items")
    // 功能：新增明细
    public Result<Void> addItem(@Valid @RequestBody CartAddRequest request) {
        cartService.addToCart(requireUserId(), request.getProductId(), request.getQuantity());
        return Result.success();
    }

    @GetMapping("/items")
    // 功能：查询明细
    public Result<List<CartItemView>> listItems() {
        return Result.success(cartService.listCartItems(requireUserId()));
    }

    @PutMapping("/items/{id}")
    // 功能：更新明细
    public Result<Void> updateItem(@PathVariable Long id, @Valid @RequestBody CartUpdateRequest request) {
        cartService.updateCartItem(requireUserId(), id, request.getQuantity(), request.getChecked());
        return Result.success();
    }

    @DeleteMapping("/items/{id}")
    // 功能：删除明细
    public Result<Void> deleteItem(@PathVariable Long id) {
        cartService.deleteCartItem(requireUserId(), id);
        return Result.success();
    }

    // 功能：获取并校验当前用户ID
    private Long requireUserId() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "Unauthorized");
        }
        return userId;
    }
}
