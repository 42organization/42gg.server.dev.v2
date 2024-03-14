package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;

public class OnPenaltyException extends ForbiddenException {
	public OnPenaltyException() {
		super(ErrorCode.ON_PENALTY.getMessage(), ErrorCode.ON_PENALTY);
	}
}
