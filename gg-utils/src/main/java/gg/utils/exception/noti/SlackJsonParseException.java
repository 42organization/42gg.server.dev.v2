package gg.utils.exception.noti;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ParseException;

public class SlackJsonParseException extends ParseException {
	public SlackJsonParseException() {
		super("json parse error in getDmChannelId()", ErrorCode.SLACK_JSON_PARSE_ERR);
	}
}
