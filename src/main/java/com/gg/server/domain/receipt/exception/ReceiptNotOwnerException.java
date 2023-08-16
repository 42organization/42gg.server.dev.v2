package com.gg.server.domain.receipt.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.ForbiddenException;

public class ReceiptNotOwnerException extends ForbiddenException {
    public ReceiptNotOwnerException() {super("아이템의 소유자가 아닙니다.", ErrorCode.RECEIPT_NOT_OWNER);}
}
