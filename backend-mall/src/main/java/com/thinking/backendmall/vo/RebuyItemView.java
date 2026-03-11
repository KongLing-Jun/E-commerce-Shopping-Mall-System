package com.thinking.backendmall.vo;

public class RebuyItemView {
    private Long productId;
    private String productName;
    private Integer requestedQty;
    private Integer addedQty;
    private String reason;

    // 功能：获取商品id
    public Long getProductId() {
        return productId;
    }

    // 功能：设置商品id
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // 功能：获取商品name
    public String getProductName() {
        return productName;
    }

    // 功能：设置商品name
    public void setProductName(String productName) {
        this.productName = productName;
    }

    // 功能：获取requestedqty
    public Integer getRequestedQty() {
        return requestedQty;
    }

    // 功能：设置requestedqty
    public void setRequestedQty(Integer requestedQty) {
        this.requestedQty = requestedQty;
    }

    // 功能：获取addedqty
    public Integer getAddedQty() {
        return addedQty;
    }

    // 功能：设置addedqty
    public void setAddedQty(Integer addedQty) {
        this.addedQty = addedQty;
    }

    // 功能：获取reason
    public String getReason() {
        return reason;
    }

    // 功能：设置reason
    public void setReason(String reason) {
        this.reason = reason;
    }
}
