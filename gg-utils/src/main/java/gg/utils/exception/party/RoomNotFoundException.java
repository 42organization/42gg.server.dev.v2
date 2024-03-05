package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class RoomNotFoundException extends NotExistException {
	public RoomNotFoundException() {
		super(ErrorCode.ROOM_NOT_FOUND.getMessage(), ErrorCode.ROOM_NOT_FOUND);
	}

	public RoomNotFoundException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}
}
