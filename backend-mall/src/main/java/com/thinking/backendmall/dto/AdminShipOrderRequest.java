package com.thinking.backendmall.dto;

public class AdminShipOrderRequest {
    private String expressNo;
    private String expressCompany;

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
