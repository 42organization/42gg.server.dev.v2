package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;

public class UserAlreadyInRoom extends RuntimeException {
	private ErrorCode errorCode;

	public UserAlreadyInRoom(ErrorCode errorCode) {
		super(ErrorCode.USER_ALREADY_IN_ROOM.getMessage());
		this.errorCode = errorCode;
	}
}
