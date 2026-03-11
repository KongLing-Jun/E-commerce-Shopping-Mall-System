package com.thinking.backendmall.common;

// Unified error codes for API responses.
public enum ErrorCode {
    // 功能：处理ok
    OK(200, "Success"),
    // 功能：处理badrequest
    BAD_REQUEST(400, "Bad request"),
    // 功能：处理unauthorized
    UNAUTHORIZED(401, "Unauthorized"),
    // 功能：处理forbidden
    FORBIDDEN(403, "Forbidden"),
    // 功能：处理notfound
    NOT_FOUND(404, "Not found"),
    // 功能：处理conflict
    CONFLICT(409, "Conflict"),
    // 功能：处理商品offshelf
    PRODUCT_OFF_SHELF(410, "Product is off shelf"),
    // 功能：处理servererror
    SERVER_ERROR(500, "Server error");

    private final int code;
    private final String message;

    // 功能：处理errorcode
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // 功能：获取code
    public int getCode() {
        return code;
    }

    // 功能：获取message
    public String getMessage() {
        return message;
    }
}
