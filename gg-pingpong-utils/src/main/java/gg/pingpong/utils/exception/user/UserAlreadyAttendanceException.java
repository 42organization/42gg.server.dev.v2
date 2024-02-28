package gg.pingpong.utils.exception.user;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.DuplicationException;

public class UserAlreadyAttendanceException extends DuplicationException {
	public UserAlreadyAttendanceException() {
		super("이미 출석한 유저입니다.", ErrorCode.USER_ALREADY_ATTENDANCE);
	}
}
