package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class UserAlreadyInRoom extends DuplicationException {
	public UserAlreadyInRoom(ErrorCode errorCode) {
		super(ErrorCode.USER_ALREADY_IN_ROOM.getMessage(), ErrorCode.USER_ALREADY_IN_ROOM);
	}
}
