package com.gg.server.domain.noti.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.ParseException;

public class SlackJsonParseException extends ParseException {
    public SlackJsonParseException() {
        super("json parse error in getDmChannelId()", ErrorCode.SLACK_JSON_PARSE_ERR);
    }
}
