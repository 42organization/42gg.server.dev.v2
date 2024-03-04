package gg.utils.exception.noti;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class SlackUserGetFailedException extends NotExistException {
	public SlackUserGetFailedException() {
		super("fail to get slack user info", ErrorCode.SLACK_USER_NOT_FOUND);
	}
}
