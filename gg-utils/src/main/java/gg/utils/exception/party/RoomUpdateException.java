package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;

public class RoomUpdateException extends ForbiddenException {
	public RoomUpdateException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}
}
