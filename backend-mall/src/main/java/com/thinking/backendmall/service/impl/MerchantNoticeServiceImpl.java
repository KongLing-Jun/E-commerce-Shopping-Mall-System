package com.thinking.backendmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thinking.backendmall.common.BusinessException;
import com.thinking.backendmall.common.PageResult;
import com.thinking.backendmall.entity.MerchantNotice;
import com.thinking.backendmall.repository.MerchantNoticeRepository;
import com.thinking.backendmall.service.MerchantNoticeService;
import com.thinking.backendmall.vo.MerchantNoticeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MerchantNoticeServiceImpl implements MerchantNoticeService {
    private static final Logger log = LoggerFactory.getLogger(MerchantNoticeServiceImpl.class);

    @Autowired
    private MerchantNoticeRepository merchantNoticeRepository;

    @Autowired
    private ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.notify.merchant-mail.enabled:false}")
    private boolean merchantMailEnabled;

    @Value("${app.notify.merchant-mail.to:}")
    private String merchantMailTo;

    @Value("${app.notify.merchant-mail.from:}")
    private String merchantMailFrom;

    @Override
    public void notifyOrderPaid(String orderNo, Long userId, String addressSnapshot) {
        String content = "New paid order: " + orderNo + ", userId=" + userId + ", address=" + addressSnapshot;
        MerchantNotice notice = new MerchantNotice();
        notice.setNoticeType("ORDER_PAID");
        notice.setOrderNo(orderNo);
        notice.setContent(content);
        notice.setStatus(0);
        notice.setCreatedAt(LocalDateTime.now());
        merchantNoticeRepository.insert(notice);
        sendMailIfEnabled(orderNo, content);
    }

    @Override
    public PageResult<MerchantNoticeView> listNotices(int page, int size) {
        Page<MerchantNotice> noticePage = new Page<>(page + 1L, size);
        LambdaQueryWrapper<MerchantNotice> wrapper = new LambdaQueryWrapper<MerchantNotice>()
                .orderByDesc(MerchantNotice::getCreatedAt);
        merchantNoticeRepository.selectPage(noticePage, wrapper);

        List<MerchantNoticeView> views = new ArrayList<>();
        for (MerchantNotice notice : noticePage.getRecords()) {
            MerchantNoticeView view = new MerchantNoticeView();
            view.setId(notice.getId());
            view.setNoticeType(notice.getNoticeType());
            view.setOrderNo(notice.getOrderNo());
            view.setContent(notice.getContent());
            view.setStatus(notice.getStatus());
            view.setCreatedAt(notice.getCreatedAt());
            views.add(view);
        }
        boolean last = noticePage.getCurrent() >= noticePage.getPages();
        return new PageResult<>(views, noticePage.getTotal(), noticePage.getPages(), page, size, last);
    }

    @Override
    public void markAsRead(Long id) {
        MerchantNotice notice = merchantNoticeRepository.selectById(id);
        if (notice == null) {
            throw new BusinessException(404, "Notice not found");
        }
        notice.setStatus(1);
        merchantNoticeRepository.updateById(notice);
    }

    private void sendMailIfEnabled(String orderNo, String content) {
        if (!merchantMailEnabled || merchantMailTo == null || merchantMailTo.isBlank()) {
            return;
        }
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("Merchant mail is enabled, but JavaMailSender is unavailable. orderNo={}", orderNo);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (merchantMailFrom != null && !merchantMailFrom.isBlank()) {
                message.setFrom(merchantMailFrom);
            }
            message.setTo(merchantMailTo);
            message.setSubject("[Mall] New paid order " + orderNo);
            message.setText(content);
            mailSender.send(message);
        } catch (Exception ex) {
            log.warn("Failed to send merchant mail for orderNo={}", orderNo, ex);
        }
    }
}
