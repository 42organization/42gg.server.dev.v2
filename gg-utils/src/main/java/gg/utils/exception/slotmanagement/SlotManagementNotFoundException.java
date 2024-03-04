package gg.utils.exception.slotmanagement;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class SlotManagementNotFoundException extends NotExistException {
	public SlotManagementNotFoundException() {
		super(ErrorCode.SLOTMANAGEMENT_NOT_FOUND.getMessage(), ErrorCode.SLOTMANAGEMENT_NOT_FOUND);
	}
}
