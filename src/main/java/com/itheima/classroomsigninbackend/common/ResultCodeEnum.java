package com.itheima.classroomsigninbackend.common;

public enum ResultCodeEnum {
    SUCCESS(200, "成功"),
    UNAUTHORIZED(401, "未授权"),
    SYSTEM_ERROR(500, "系统异常");

    private final int code;
    private final String message;

    ResultCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
