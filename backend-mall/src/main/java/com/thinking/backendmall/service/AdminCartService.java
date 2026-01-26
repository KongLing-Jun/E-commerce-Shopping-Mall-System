package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.vo.AdminCartItemView;

public interface AdminCartService {
    PageResult<AdminCartItemView> listCartItems(Long userId, int page, int size);

    void deleteCartItem(Long cartItemId);
}
