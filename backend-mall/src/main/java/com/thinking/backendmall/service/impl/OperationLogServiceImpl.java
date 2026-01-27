package com.thinking.backendmall.service.impl;

import com.thinking.backendmall.config.security.AuthContext;
import com.thinking.backendmall.entity.OperationLog;
import com.thinking.backendmall.repository.OperationLogRepository;
import com.thinking.backendmall.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Override
    public void record(String action, String target, String detail) {
        String roleKey = AuthContext.getRoleKey();
        if (roleKey == null || !"ADMIN".equals(roleKey)) {
            return;
        }
        OperationLog log = new OperationLog();
        log.setAdminId(AuthContext.getUserId());
        log.setAdminUsername(AuthContext.getUsername());
        log.setAction(action);
        log.setTarget(target);
        log.setDetail(detail);
        log.setIp(resolveIp());
        log.setCreatedAt(LocalDateTime.now());
        operationLogRepository.insert(log);
    }

    private String resolveIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        if (request == null) {
            return null;
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
