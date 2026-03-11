package com.thinking.backendmall.vo;

import java.util.ArrayList;
import java.util.List;

public class RebuyResponse {
    private List<RebuyItemView> addedItems = new ArrayList<>();
    private List<RebuyItemView> skippedItems = new ArrayList<>();

    // 功能：获取added明细
    public List<RebuyItemView> getAddedItems() {
        return addedItems;
    }

    // 功能：设置added明细
    public void setAddedItems(List<RebuyItemView> addedItems) {
        this.addedItems = addedItems;
    }

    // 功能：获取skipped明细
    public List<RebuyItemView> getSkippedItems() {
        return skippedItems;
    }

    // 功能：设置skipped明细
    public void setSkippedItems(List<RebuyItemView> skippedItems) {
        this.skippedItems = skippedItems;
    }
}
