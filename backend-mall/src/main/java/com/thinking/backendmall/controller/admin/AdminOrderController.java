package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.dto.AdminShipOrderRequest;
import com.thinking.backendmall.service.AdminOrderService;
import com.thinking.backendmall.service.OperationLogService;
import com.thinking.backendmall.vo.AdminOrderView;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    @Autowired
    private OperationLogService operationLogService;

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
    public Result<Void> shipOrder(@PathVariable String orderNo,
                                  @RequestBody(required = false) AdminShipOrderRequest request) {
        String expressNo = request == null ? null : request.getExpressNo();
        String expressCompany = request == null ? null : request.getExpressCompany();
        adminOrderService.shipOrder(orderNo, expressNo, expressCompany);
        operationLogService.record("ORDER_SHIP", "order:" + orderNo,
                "ship expressNo=" + expressNo + ",expressCompany=" + expressCompany);
        return Result.success();
    }

    @GetMapping("/export")
    @PreAuthorize("@permissionService.hasPerm('admin:orders:export')")
    public void exportOrders(@RequestParam(required = false) String orderNo,
                             @RequestParam(required = false) Long userId,
                             @RequestParam(required = false) Integer status,
                             HttpServletResponse response) throws IOException {
        byte[] data = adminOrderService.exportOrders(orderNo, userId, status);
        operationLogService.record("ORDER_EXPORT", "order:export", "orderNo=" + orderNo + ",userId=" + userId + ",status=" + status);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=orders.xlsx");
        response.getOutputStream().write(data);
        response.flushBuffer();
    }
}
