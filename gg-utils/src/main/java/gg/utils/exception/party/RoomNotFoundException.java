package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class RoomNotFoundException extends NotExistException {
	public RoomNotFoundException(String message) {
		super(message, ErrorCode.ROOM_NOT_FOUND);
	}
}
