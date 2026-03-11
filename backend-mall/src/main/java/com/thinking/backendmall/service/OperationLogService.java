package com.thinking.backendmall.service;

public interface OperationLogService {
    // 功能：记录数据
    void record(String action, String target, String detail);
}
