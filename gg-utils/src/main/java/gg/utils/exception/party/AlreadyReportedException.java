package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class AlreadyReportedException extends DuplicationException {
	public AlreadyReportedException() {
		super(ErrorCode.ALREADY_REPORTED.getMessage(), ErrorCode.ALREADY_REPORTED);
	}
}
