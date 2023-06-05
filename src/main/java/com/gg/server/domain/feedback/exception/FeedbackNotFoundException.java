package com.gg.server.domain.feedback.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.CustomRuntimeException;

public class FeedbackNotFoundException extends CustomRuntimeException {
    public FeedbackNotFoundException() {
        super(ErrorCode.FEEDBACK_NOT_FOUND.getMessage(), ErrorCode.FEEDBACK_NOT_FOUND);
    }
}
