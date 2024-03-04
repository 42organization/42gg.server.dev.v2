package gg.utils.exception.announcement;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class AnnounceDupException extends DuplicationException {
	public AnnounceDupException() {
		super(ErrorCode.ANNOUNCE_DUPLICATE.getMessage(), ErrorCode.ANNOUNCE_DUPLICATE);
	}
}
