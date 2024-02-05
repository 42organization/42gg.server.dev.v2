package com.gg.server.domain.announcement.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.CustomRuntimeException;

public class AnnounceNotFoundException extends CustomRuntimeException {
	public AnnounceNotFoundException() {
		super(ErrorCode.ANNOUNCE_NOT_FOUND.getMessage(), ErrorCode.ANNOUNCE_NOT_FOUND);
	}
}
