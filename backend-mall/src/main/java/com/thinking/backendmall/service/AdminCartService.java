package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.vo.AdminCartItemView;

public interface AdminCartService {
    // 功能：查询购物车明细
    PageResult<AdminCartItemView> listCartItems(Long userId, int page, int size);

    // 功能：删除购物车明细
    void deleteCartItem(Long cartItemId);
}
