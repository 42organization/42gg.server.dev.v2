package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class UserNotFoundException extends NotExistException {
	public UserNotFoundException() {
		super(ErrorCode.USER_NOT_EXIST.getMessage(), ErrorCode.USER_NOT_EXIST);
	}

}
