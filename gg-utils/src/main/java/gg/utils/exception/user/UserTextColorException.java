package gg.utils.exception.user;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.CustomRuntimeException;

public class UserTextColorException extends CustomRuntimeException {
	public UserTextColorException() {
		super("user text color code is not valid", ErrorCode.USER_TEXT_COLOR_WRONG_TYPE);
	}
}
