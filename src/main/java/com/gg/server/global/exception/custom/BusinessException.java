package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class BusinessException extends RuntimeException {
    private String code;
    private String message;

    public BusinessException(String code) {
        this.code = code;
        this.message = "해당 데이터는 현재 null입니다";
    }
}
