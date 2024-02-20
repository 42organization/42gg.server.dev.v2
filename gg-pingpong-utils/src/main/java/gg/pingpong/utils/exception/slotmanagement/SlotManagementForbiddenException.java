package gg.pingpong.utils.exception.slotmanagement;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.ForbiddenException;

public class SlotManagementForbiddenException extends ForbiddenException {
	public SlotManagementForbiddenException() {
		super(ErrorCode.SLOTMANAGEMENT_FORBIDDEN.getMessage(), ErrorCode.SLOTMANAGEMENT_FORBIDDEN);
	}
}
