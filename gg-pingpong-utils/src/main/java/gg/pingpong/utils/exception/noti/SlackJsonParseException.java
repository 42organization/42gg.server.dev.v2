package gg.pingpong.utils.exception.noti;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.ParseException;

public class SlackJsonParseException extends ParseException {
	public SlackJsonParseException() {
		super("json parse error in getDmChannelId()", ErrorCode.SLACK_JSON_PARSE_ERR);
	}
}
