package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

public class RoomNotEnoughPeopleException extends BusinessException {
	public RoomNotEnoughPeopleException() {
		super(ErrorCode.ROOM_NOT_ENOUGH_PEOPLE.getMessage(), ErrorCode.ROOM_NOT_ENOUGH_PEOPLE);
	}
}
