package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

public class RoomNotParticipantException extends BusinessException {
	public RoomNotParticipantException() {
		super(ErrorCode.ROOM_NOT_PARTICIPANT.getMessage(), ErrorCode.ROOM_NOT_PARTICIPANT);
	}
}
