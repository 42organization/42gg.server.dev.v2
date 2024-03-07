package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class AlredayReportedRoomException extends DuplicationException {
	public AlredayReportedRoomException() {
		super(ErrorCode.ALREADY_REPORTED_ROOM.getMessage(), ErrorCode.ALREADY_REPORTED_ROOM);
	}
}
