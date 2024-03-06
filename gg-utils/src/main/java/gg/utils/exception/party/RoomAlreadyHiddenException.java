package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class RoomAlreadyHiddenException extends DuplicationException {
	public RoomAlreadyHiddenException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public RoomAlreadyHiddenException() {
		super(ErrorCode.ROOM_ALREADY_HIDDEN.getMessage(), ErrorCode.ROOM_ALREADY_HIDDEN);
	}
}
