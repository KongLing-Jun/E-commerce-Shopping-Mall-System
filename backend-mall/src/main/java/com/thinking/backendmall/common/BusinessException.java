package com.thinking.backendmall.common;

public class BusinessException extends RuntimeException {
    private int code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode == null ? ErrorCode.SERVER_ERROR.getMessage() : errorCode.getMessage());
        this.code = errorCode == null ? ErrorCode.SERVER_ERROR.getCode() : errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
