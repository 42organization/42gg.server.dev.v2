package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

public class SelfReportException extends BusinessException {
	public SelfReportException() {
		super(ErrorCode.SELF_REPORT.getMessage(), ErrorCode.SELF_REPORT);
	}
}
