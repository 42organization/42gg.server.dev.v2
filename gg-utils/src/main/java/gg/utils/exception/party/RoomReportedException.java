package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class RoomReportedException extends NotExistException {
	public RoomReportedException(String message) {
		super(message, ErrorCode.ROOM_REPORTED_ERROR);
	}
}
