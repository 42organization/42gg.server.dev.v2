package gg.pingpong.utils.exception.slotmanagement;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class SlotManagementNotFoundException extends NotExistException {
	public SlotManagementNotFoundException() {
		super(ErrorCode.SLOTMANAGEMENT_NOT_FOUND.getMessage(), ErrorCode.SLOTMANAGEMENT_NOT_FOUND);
	}
}
