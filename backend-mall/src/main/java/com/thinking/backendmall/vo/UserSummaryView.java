package com.thinking.backendmall.vo;

public class UserSummaryView {
    private long totalOrders;
    private long pendingOrders;
    private long paidOrders;
    private long shippedOrders;
    private long completedOrders;
    private long favoriteCount;
    private long footprintCount;

    // 功能：获取总计订单
    public long getTotalOrders() {
        return totalOrders;
    }

    // 功能：设置总计订单
    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    // 功能：获取pending订单
    public long getPendingOrders() {
        return pendingOrders;
    }

    // 功能：设置pending订单
    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    // 功能：获取paid订单
    public long getPaidOrders() {
        return paidOrders;
    }

    // 功能：设置paid订单
    public void setPaidOrders(long paidOrders) {
        this.paidOrders = paidOrders;
    }

    // 功能：获取shipped订单
    public long getShippedOrders() {
        return shippedOrders;
    }

    // 功能：设置shipped订单
    public void setShippedOrders(long shippedOrders) {
        this.shippedOrders = shippedOrders;
    }

    // 功能：获取completed订单
    public long getCompletedOrders() {
        return completedOrders;
    }

    // 功能：设置completed订单
    public void setCompletedOrders(long completedOrders) {
        this.completedOrders = completedOrders;
    }

    // 功能：获取收藏数量
    public long getFavoriteCount() {
        return favoriteCount;
    }

    // 功能：设置收藏数量
    public void setFavoriteCount(long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    // 功能：获取足迹数量
    public long getFootprintCount() {
        return footprintCount;
    }

    // 功能：设置足迹数量
    public void setFootprintCount(long footprintCount) {
        this.footprintCount = footprintCount;
    }
}
