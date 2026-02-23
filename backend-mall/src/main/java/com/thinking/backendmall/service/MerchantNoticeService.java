package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.vo.MerchantNoticeView;

public interface MerchantNoticeService {
    void notifyOrderPaid(String orderNo, Long userId, String addressSnapshot);

    PageResult<MerchantNoticeView> listNotices(int page, int size);

    void markAsRead(Long id);
}
