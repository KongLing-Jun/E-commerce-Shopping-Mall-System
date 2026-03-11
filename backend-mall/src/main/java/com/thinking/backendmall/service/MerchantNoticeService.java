package com.thinking.backendmall.service;

import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.vo.MerchantNoticeView;

public interface MerchantNoticeService {
    // 功能：处理notify订单paid
    void notifyOrderPaid(String orderNo, Long userId, String addressSnapshot);

    // 功能：查询通知
    PageResult<MerchantNoticeView> listNotices(int page, int size);

    // 功能：处理markasread
    void markAsRead(Long id);
}
