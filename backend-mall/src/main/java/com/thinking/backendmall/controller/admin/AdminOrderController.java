package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.service.AdminOrderService;
import com.thinking.backendmall.vo.AdminOrderView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    @GetMapping
    @PreAuthorize("@permissionService.hasPerm('admin:orders:list')")
    public Result<PageResult<AdminOrderView>> listOrders(@RequestParam(required = false) String orderNo,
                                                         @RequestParam(required = false) Long userId,
                                                         @RequestParam(required = false) Integer status,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminOrderService.listOrders(orderNo, userId, status, page, size));
    }

    @PostMapping("/{orderNo}/ship")
    @PreAuthorize("@permissionService.hasPerm('admin:orders:ship')")
    public Result<Void> shipOrder(@PathVariable String orderNo) {
        adminOrderService.shipOrder(orderNo);
        return Result.success();
    }
}
