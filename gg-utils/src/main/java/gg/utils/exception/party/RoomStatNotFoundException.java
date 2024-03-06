package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class RoomStatNotFoundException extends NotExistException {
	public RoomStatNotFoundException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public RoomStatNotFoundException() {
		super(ErrorCode.ROOMSTAT_NOT_FOUND.getMessage(), ErrorCode.ROOMSTAT_NOT_FOUND);
	}
}
