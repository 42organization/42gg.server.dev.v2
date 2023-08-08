package com.gg.server.domain.receipt.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;

public class ItemStatusException extends BusinessException {
    public ItemStatusException() {super("아이템 상태 오류입니다.", ErrorCode.RECEIPT_STATUS_NOT_MATCHED);}
}
