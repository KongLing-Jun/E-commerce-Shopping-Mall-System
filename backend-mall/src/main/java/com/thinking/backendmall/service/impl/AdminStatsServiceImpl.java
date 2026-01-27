package com.thinking.backendmall.service.impl;

import com.thinking.backendmall.repository.OrderItemRepository;
import com.thinking.backendmall.repository.OrderRepository;
import com.thinking.backendmall.service.AdminStatsService;
import com.thinking.backendmall.vo.StatsOverview;
import com.thinking.backendmall.vo.TopProductView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminStatsServiceImpl implements AdminStatsService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public StatsOverview getOverview() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Long todayOrders = orderRepository.countByCreatedAtBetween(start, end);
        BigDecimal totalSales = orderRepository.sumPaidAmount();
        List<TopProductView> topProducts = orderItemRepository.selectTopProducts(10);

        StatsOverview overview = new StatsOverview();
        overview.setTodayOrders(todayOrders == null ? 0L : todayOrders);
        overview.setTotalSales(totalSales == null ? BigDecimal.ZERO : totalSales);
        overview.setTopProducts(topProducts);
        return overview;
    }
}
