package gg.utils.exception.feedback;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.CustomRuntimeException;

public class FeedbackNotFoundException extends CustomRuntimeException {
	public FeedbackNotFoundException() {
		super(ErrorCode.FEEDBACK_NOT_FOUND.getMessage(), ErrorCode.FEEDBACK_NOT_FOUND);
	}
}
