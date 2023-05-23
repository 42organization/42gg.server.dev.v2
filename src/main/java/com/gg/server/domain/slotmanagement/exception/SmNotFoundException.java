package com.gg.server.domain.slotmanagement.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class SmNotFoundException extends NotExistException {
    public SmNotFoundException() {
        super(ErrorCode.SM_NOT_FOUND.getMessage(), ErrorCode.SM_NOT_FOUND);
    }
}
