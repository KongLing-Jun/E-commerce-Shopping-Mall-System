package com.thinking.backendmall.vo;

import java.util.ArrayList;
import java.util.List;

public class RebuyResponse {
    private List<RebuyItemView> addedItems = new ArrayList<>();
    private List<RebuyItemView> skippedItems = new ArrayList<>();

    public List<RebuyItemView> getAddedItems() {
        return addedItems;
    }

    public void setAddedItems(List<RebuyItemView> addedItems) {
        this.addedItems = addedItems;
    }

    public List<RebuyItemView> getSkippedItems() {
        return skippedItems;
    }

    public void setSkippedItems(List<RebuyItemView> skippedItems) {
        this.skippedItems = skippedItems;
    }
}
