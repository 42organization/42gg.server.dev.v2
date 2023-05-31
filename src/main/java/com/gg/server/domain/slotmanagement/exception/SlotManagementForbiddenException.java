package com.gg.server.domain.slotmanagement.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.ForbiddenException;

public class SlotManagementForbiddenException extends ForbiddenException {
    public SlotManagementForbiddenException() {
        super(ErrorCode.SLOTMANAGEMENT_FORBIDDEN.getMessage(), ErrorCode.SLOTMANAGEMENT_FORBIDDEN);
    }
}
