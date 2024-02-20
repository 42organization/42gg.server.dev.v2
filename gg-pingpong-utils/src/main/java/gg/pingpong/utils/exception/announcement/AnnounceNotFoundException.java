package gg.pingpong.utils.exception.announcement;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.CustomRuntimeException;

public class AnnounceNotFoundException extends CustomRuntimeException {
	public AnnounceNotFoundException() {
		super(ErrorCode.ANNOUNCE_NOT_FOUND.getMessage(), ErrorCode.ANNOUNCE_NOT_FOUND);
	}
}
