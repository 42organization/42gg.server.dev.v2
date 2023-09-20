package com.gg.server.domain.item.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;

public class InsufficientGgcoinException extends BusinessException {
    public InsufficientGgcoinException() {
        super("GGcoin이 부족합니다.", ErrorCode.INSUFFICIENT_GGCOIN);
    }
}
