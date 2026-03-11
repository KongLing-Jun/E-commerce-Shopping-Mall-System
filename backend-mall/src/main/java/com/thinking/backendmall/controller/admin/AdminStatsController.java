package com.thinking.backendmall.controller.admin;

import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.service.AdminStatsService;
import com.thinking.backendmall.vo.StatsOverview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired
    private AdminStatsService adminStatsService;

    @GetMapping("/overview")
    @PreAuthorize("@permissionService.hasPerm('admin:stats:view')")
    // 功能：处理概览
    public Result<StatsOverview> overview() {
        return Result.success(adminStatsService.getOverview());
    }
}
