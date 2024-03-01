package gg.utils.exception.announcement;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.CustomRuntimeException;

public class AnnounceNotFoundException extends CustomRuntimeException {
	public AnnounceNotFoundException() {
		super(ErrorCode.ANNOUNCE_NOT_FOUND.getMessage(), ErrorCode.ANNOUNCE_NOT_FOUND);
	}
}
