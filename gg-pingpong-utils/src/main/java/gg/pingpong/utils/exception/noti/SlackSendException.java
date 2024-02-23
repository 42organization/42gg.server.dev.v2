package gg.pingpong.utils.exception.noti;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.SendException;

public class SlackSendException extends SendException {
	public SlackSendException() {
		super("fail to send notification", ErrorCode.SLACK_SEND_FAIL);
	}
}
