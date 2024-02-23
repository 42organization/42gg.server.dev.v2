package gg.pingpong.utils.exception.user;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class UserNotFoundException extends NotExistException {
	public UserNotFoundException() {
		super("해당 유저가 없습니다.", ErrorCode.USER_NOT_FOUND);
	}
}
