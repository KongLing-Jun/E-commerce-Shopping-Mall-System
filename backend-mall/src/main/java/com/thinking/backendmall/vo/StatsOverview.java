package com.thinking.backendmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class StatsOverview {
    private Long todayOrders;
    private BigDecimal totalSales;
    private List<TopProductView> topProducts;

    public Long getTodayOrders() {
        return todayOrders;
    }

    public void setTodayOrders(Long todayOrders) {
        this.todayOrders = todayOrders;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public List<TopProductView> getTopProducts() {
        return topProducts;
    }

    public void setTopProducts(List<TopProductView> topProducts) {
        this.topProducts = topProducts;
    }
}
