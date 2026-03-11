package com.thinking.backendmall.common;

public class Result<T> {
    private int code;
    private String message;
    private T data;

    // 功能：获取code
    public int getCode() {
        return code;
    }

    // 功能：设置code
    public void setCode(int code) {
        this.code = code;
    }

    // 功能：获取message
    public String getMessage() {
        return message;
    }

    // 功能：设置message
    public void setMessage(String message) {
        this.message = message;
    }

    // 功能：获取数据
    public T getData() {
        return data;
    }

    // 功能：设置数据
    public void setData(T data) {
        this.data = data;
    }

    // 功能：处理success
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("Success");
        result.setData(data);
        return result;
    }

    // 功能：处理success
    public static <T> Result<T> success() {
        return success(null);
    }

    // 功能：处理error
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    // 功能：处理error
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // 功能：处理error
    public static <T> Result<T> error(ErrorCode errorCode) {
        if (errorCode == null) {
            return error(500, "Server error");
        }
        return error(errorCode.getCode(), errorCode.getMessage());
    }
}
