package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class RoomAlreadyHiddenException extends DuplicationException {
	public RoomAlreadyHiddenException() {
		super("이미 숨겨진 방입니다.", ErrorCode.ROOM_ALREADY_HIDDEN);
	}
}
