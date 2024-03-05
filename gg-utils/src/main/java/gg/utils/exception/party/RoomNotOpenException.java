package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class RoomNotOpenException extends NotExistException {
	public RoomNotOpenException() {
		super(ErrorCode.ROOM_NOT_OPEN.getMessage(), ErrorCode.ROOM_NOT_OPEN);
	}

	public RoomNotOpenException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}
}
