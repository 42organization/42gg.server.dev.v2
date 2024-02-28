package gg.pingpong.utils.exception.user;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.CustomRuntimeException;

public class UserTextColorException extends CustomRuntimeException {
	public UserTextColorException() {
		super("user text color code is not valid", ErrorCode.USER_TEXT_COLOR_WRONG_TYPE);
	}
}
