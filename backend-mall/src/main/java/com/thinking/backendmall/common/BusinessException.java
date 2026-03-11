package com.thinking.backendmall.common;

public class BusinessException extends RuntimeException {
    private int code;

    // 功能：处理businessexception
    public BusinessException(String message) {
        // 功能：处理super
        super(message);
        this.code = 500;
    }

    // 功能：处理businessexception
    public BusinessException(int code, String message) {
        // 功能：处理super
        super(message);
        this.code = code;
    }

    // 功能：处理businessexception
    public BusinessException(ErrorCode errorCode) {
        // 功能：处理super
        super(errorCode == null ? ErrorCode.SERVER_ERROR.getMessage() : errorCode.getMessage());
        this.code = errorCode == null ? ErrorCode.SERVER_ERROR.getCode() : errorCode.getCode();
    }

    // 功能：获取code
    public int getCode() {
        return code;
    }
}
