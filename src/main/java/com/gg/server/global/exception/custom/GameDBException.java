package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class GameDBException extends CustomRuntimeException {
    public GameDBException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
