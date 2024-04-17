package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.InvalidParameterException;

public class RoomMinMaxTime extends InvalidParameterException {
	public RoomMinMaxTime(ErrorCode errorCode) {
		super(ErrorCode.ROOM_MIN_MAX_TIME.getMessage(), ErrorCode.ROOM_MIN_MAX_TIME);
	}
}
