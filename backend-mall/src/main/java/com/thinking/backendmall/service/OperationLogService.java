package com.thinking.backendmall.service;

public interface OperationLogService {
    void record(String action, String target, String detail);
}
