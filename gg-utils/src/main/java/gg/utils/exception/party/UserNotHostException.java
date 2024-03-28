package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;

public class UserNotHostException extends ForbiddenException {
	public UserNotHostException() {
		super(ErrorCode.USER_NOT_HOST.getMessage(), ErrorCode.USER_NOT_HOST);
	}
}
