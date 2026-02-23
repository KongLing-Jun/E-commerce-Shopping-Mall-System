package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.service.MerchantNoticeService;
import com.thinking.backendmall.vo.MerchantNoticeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/notices")
public class AdminNoticeController {

    @Autowired
    private MerchantNoticeService merchantNoticeService;

    @GetMapping
    @PreAuthorize("@permissionService.hasPerm('admin:orders:list')")
    public Result<PageResult<MerchantNoticeView>> list(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return Result.success(merchantNoticeService.listNotices(page, size));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("@permissionService.hasPerm('admin:orders:list')")
    public Result<Void> markAsRead(@PathVariable Long id) {
        merchantNoticeService.markAsRead(id);
        return Result.success();
    }
}
