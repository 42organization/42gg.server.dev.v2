package com.gg.server.domain.announcement.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.CustomRuntimeException;

public class AnNotFoundException extends CustomRuntimeException {
    public AnNotFoundException() {
        super(ErrorCode.AN_NOT_FOUND.getMessage(), ErrorCode.AN_NOT_FOUND);
    }
}