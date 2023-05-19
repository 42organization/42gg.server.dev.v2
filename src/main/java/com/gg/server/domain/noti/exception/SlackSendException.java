package com.gg.server.domain.noti.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.SendException;

public class SlackSendException extends SendException {
    public SlackSendException() {
        super("fail to send notification", ErrorCode.SLACK_SEND_FAIL);
    }
}
