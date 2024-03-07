package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class RoomSameStatusException extends DuplicationException {
	public RoomSameStatusException() {
		super(ErrorCode.ROOM_SAME_STATUS.getMessage(), ErrorCode.ROOM_SAME_STATUS);
	}
}
