package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class RoomNotParticipantException extends NotExistException {
	public RoomNotParticipantException() {
		super(ErrorCode.ROOM_NOT_PARTICIPANT.getMessage(), ErrorCode.ROOM_NOT_PARTICIPANT);
	}
}
