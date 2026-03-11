package com.thinking.backendmall.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("order_delivery")
public class OrderDelivery {
    @TableId("order_id")
    private Long orderId;
    private String expressNo;
    private String expressCompany;

    // 功能：获取订单id
    public Long getOrderId() {
        return orderId;
    }

    // 功能：设置订单id
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    // 功能：获取物流no
    public String getExpressNo() {
        return expressNo;
    }

    // 功能：设置物流no
    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    // 功能：获取物流公司
    public String getExpressCompany() {
        return expressCompany;
    }

    // 功能：设置物流公司
    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }
}
