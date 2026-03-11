package com.thinking.backendmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class StatsOverview {
    private Long todayOrders;
    private BigDecimal totalSales;
    private List<TopProductView> topProducts;

    // 功能：获取today订单
    public Long getTodayOrders() {
        return todayOrders;
    }

    // 功能：设置today订单
    public void setTodayOrders(Long todayOrders) {
        this.todayOrders = todayOrders;
    }

    // 功能：获取总计sales
    public BigDecimal getTotalSales() {
        return totalSales;
    }

    // 功能：设置总计sales
    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    // 功能：获取top商品
    public List<TopProductView> getTopProducts() {
        return topProducts;
    }

    // 功能：设置top商品
    public void setTopProducts(List<TopProductView> topProducts) {
        this.topProducts = topProducts;
    }
}
