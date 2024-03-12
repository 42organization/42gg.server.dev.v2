package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;

public class SelfReportException extends ForbiddenException {
	public SelfReportException() {
		super(ErrorCode.SELF_REPORT.getMessage(), ErrorCode.SELF_REPORT);
	}
}
