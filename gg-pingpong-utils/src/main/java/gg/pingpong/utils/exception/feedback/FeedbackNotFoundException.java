package gg.pingpong.utils.exception.feedback;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.CustomRuntimeException;

public class FeedbackNotFoundException extends CustomRuntimeException {
	public FeedbackNotFoundException() {
		super(ErrorCode.FEEDBACK_NOT_FOUND.getMessage(), ErrorCode.FEEDBACK_NOT_FOUND);
	}
}
