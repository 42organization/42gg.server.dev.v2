package gg.utils.exception.user;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class UserAlreadyAttendanceException extends DuplicationException {
	public UserAlreadyAttendanceException() {
		super("이미 출석한 유저입니다.", ErrorCode.USER_ALREADY_ATTENDANCE);
	}
}
