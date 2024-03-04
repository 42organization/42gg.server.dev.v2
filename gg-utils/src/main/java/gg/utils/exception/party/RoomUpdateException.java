package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;

public class RoomUpdateException extends ForbiddenException {
	public RoomUpdateException() {
		super(ErrorCode.ROOM_FINISHED.getMessage(), ErrorCode.ROOM_FINISHED);
	}
}
