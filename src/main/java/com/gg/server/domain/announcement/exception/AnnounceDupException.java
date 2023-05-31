package com.gg.server.domain.announcement.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DuplicationException;

public class AnnounceDupException extends DuplicationException {
    public AnnounceDupException() {
        super(ErrorCode.ANNOUNCE_DUPLICATE.getMessage(), ErrorCode.ANNOUNCE_DUPLICATE);
    }
}
