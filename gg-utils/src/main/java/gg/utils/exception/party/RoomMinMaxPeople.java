package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.InvalidParameterException;

public class RoomMinMaxPeople extends InvalidParameterException {
	public RoomMinMaxPeople(ErrorCode errorCode) {
		super(ErrorCode.ROOM_MIN_MAX_PEOPLE.getMessage(), ErrorCode.ROOM_MIN_MAX_PEOPLE);
	}
}
