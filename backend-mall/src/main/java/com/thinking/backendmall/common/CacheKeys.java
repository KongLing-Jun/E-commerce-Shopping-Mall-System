package com.thinking.backendmall.common;

public final class CacheKeys {
    public static final String HOME_BANNERS = "home:banners";
    public static final String HOME_CATEGORIES = "home:categories";
    public static final String HOME_RECOMMEND = "home:recommend";
    public static final String PRODUCT_DETAIL_PREFIX = "product:detail:";
    public static final String PRODUCT_SEARCH_PREFIX = "product:search:";

    private CacheKeys() {
    }

    public static String productDetail(Long productId) {
        return PRODUCT_DETAIL_PREFIX + productId;
    }

    public static String productSearch(String keyword, Long categoryId, int page, int size) {
        String safeKeyword = keyword == null || keyword.isBlank() ? "all" : keyword.trim().toLowerCase();
        String safeCategory = categoryId == null ? "all" : String.valueOf(categoryId);
        return PRODUCT_SEARCH_PREFIX + safeKeyword + ":" + safeCategory + ":" + page + ":" + size;
    }
}
