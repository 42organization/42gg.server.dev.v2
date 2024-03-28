package gg.utils.exception.recruitment;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.CustomRuntimeException;

public class InvalidCheckListException extends CustomRuntimeException {
	public InvalidCheckListException() {
		super(ErrorCode.INVALID_CHECKLIST.getMessage(), ErrorCode.INVALID_CHECKLIST);
	}
}
