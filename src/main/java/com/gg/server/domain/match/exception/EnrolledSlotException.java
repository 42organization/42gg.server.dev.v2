package com.gg.server.domain.match.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DuplicationException;

public class EnrolledSlotException extends DuplicationException {
    public EnrolledSlotException() {
        super("이미 등록된 슬롯입니다.", ErrorCode.SLOT_ENROLLED);
    }
}
