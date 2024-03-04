package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class RoomReportedException extends NotExistException {
	public RoomReportedException() {
		super(ErrorCode.ROOM_REPORTED_ERROR.getMessage(), ErrorCode.ROOM_REPORTED_ERROR);
	}
}
