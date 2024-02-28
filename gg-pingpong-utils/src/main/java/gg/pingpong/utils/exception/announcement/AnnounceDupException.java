package gg.pingpong.utils.exception.announcement;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.DuplicationException;

public class AnnounceDupException extends DuplicationException {
	public AnnounceDupException() {
		super(ErrorCode.ANNOUNCE_DUPLICATE.getMessage(), ErrorCode.ANNOUNCE_DUPLICATE);
	}
}
