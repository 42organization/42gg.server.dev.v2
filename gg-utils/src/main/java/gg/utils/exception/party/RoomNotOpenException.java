package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

public class RoomNotOpenException extends BusinessException {
	public RoomNotOpenException() {
		super(ErrorCode.ROOM_NOT_OPEN.getMessage(), ErrorCode.ROOM_NOT_OPEN);
	}
}
