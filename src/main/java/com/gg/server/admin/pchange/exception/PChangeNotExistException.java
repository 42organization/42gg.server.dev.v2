package com.gg.server.admin.pchange.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class PChangeNotExistException extends NotExistException {
    public PChangeNotExistException() {
        super(ErrorCode.PCHANGE_NOT_FOUND.getMessage(), ErrorCode.PCHANGE_NOT_FOUND);
    }
}
