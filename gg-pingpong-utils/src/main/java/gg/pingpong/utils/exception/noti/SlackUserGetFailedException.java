package gg.pingpong.utils.exception.noti;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class SlackUserGetFailedException extends NotExistException {
	public SlackUserGetFailedException() {
		super("fail to get slack user info", ErrorCode.SLACK_USER_NOT_FOUND);
	}
}
