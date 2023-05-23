package com.gg.server.domain.announcement.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DuplicationException;

public class AnDupException extends DuplicationException {
    public AnDupException() {
        super(ErrorCode.AN_DUP.getMessage(), ErrorCode.AN_DUP);
    }
}
