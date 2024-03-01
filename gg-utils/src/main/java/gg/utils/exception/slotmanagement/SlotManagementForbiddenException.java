package gg.utils.exception.slotmanagement;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;

public class SlotManagementForbiddenException extends ForbiddenException {
	public SlotManagementForbiddenException() {
		super(ErrorCode.SLOTMANAGEMENT_FORBIDDEN.getMessage(), ErrorCode.SLOTMANAGEMENT_FORBIDDEN);
	}
}
