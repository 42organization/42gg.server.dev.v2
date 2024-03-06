package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;

public class RoomNotEnoughPeopleException extends ForbiddenException {
	public RoomNotEnoughPeopleException() {
		super(ErrorCode.ROOM_NOT_ENOUGH_PEOPLE.getMessage(), ErrorCode.ROOM_NOT_ENOUGH_PEOPLE);
	}
}
