package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;

public class RoomMinMaxPeople extends RuntimeException {
	private ErrorCode errorCode;

	public RoomMinMaxPeople(ErrorCode errorCode) {
		super(ErrorCode.ROOM_MIN_MAX_PEOPLE.getMessage());
		this.errorCode = errorCode;
	}
}
