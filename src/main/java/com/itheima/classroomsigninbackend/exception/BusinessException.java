package com.itheima.classroomsigninbackend.exception;

import com.itheima.classroomsigninbackend.common.ResultCodeEnum;

public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(ResultCodeEnum resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
