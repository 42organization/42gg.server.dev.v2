package com.gg.server.domain.slotmanagement.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class SlotManagementNotFoundException extends NotExistException {
	public SlotManagementNotFoundException() {
		super(ErrorCode.SLOTMANAGEMENT_NOT_FOUND.getMessage(), ErrorCode.SLOTMANAGEMENT_NOT_FOUND);
	}
}
