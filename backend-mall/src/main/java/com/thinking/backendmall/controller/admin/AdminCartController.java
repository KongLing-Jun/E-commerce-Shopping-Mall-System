package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.service.AdminCartService;
import com.thinking.backendmall.vo.AdminCartItemView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/carts")
public class AdminCartController {

    @Autowired
    private AdminCartService adminCartService;

    @GetMapping
    @PreAuthorize("@permissionService.hasPerm('admin:carts:list')")
    public Result<PageResult<AdminCartItemView>> listCartItems(@RequestParam(required = false) Long userId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminCartService.listCartItems(userId, page, size));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionService.hasPerm('admin:carts:delete')")
    public Result<Void> deleteCartItem(@PathVariable Long id) {
        adminCartService.deleteCartItem(id);
        return Result.success();
    }
}
