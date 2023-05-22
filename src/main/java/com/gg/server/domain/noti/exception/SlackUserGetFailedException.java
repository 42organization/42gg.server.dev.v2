package com.gg.server.domain.noti.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class SlackUserGetFailedException extends NotExistException {
    public SlackUserGetFailedException() {
        super("fail to get slack user info", ErrorCode.SLACK_USER_NOT_FOUND);
    }
}
