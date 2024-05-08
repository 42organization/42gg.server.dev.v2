package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class ChangeSameStatusException extends DuplicationException {
	public ChangeSameStatusException() {
		super(ErrorCode.CHANGE_SAME_STATUS.getMessage(), ErrorCode.CHANGE_SAME_STATUS);
	}
}
