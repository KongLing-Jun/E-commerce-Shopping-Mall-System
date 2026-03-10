package com.thinking.backendmall.vo;

public class RebuyItemView {
    private Long productId;
    private String productName;
    private Integer requestedQty;
    private Integer addedQty;
    private String reason;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getRequestedQty() {
        return requestedQty;
    }

    public void setRequestedQty(Integer requestedQty) {
        this.requestedQty = requestedQty;
    }

    public Integer getAddedQty() {
        return addedQty;
    }

    public void setAddedQty(Integer addedQty) {
        this.addedQty = addedQty;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
