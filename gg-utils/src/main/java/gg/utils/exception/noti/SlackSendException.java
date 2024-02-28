package gg.utils.exception.noti;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.SendException;

public class SlackSendException extends SendException {
	public SlackSendException() {
		super("fail to send notification", ErrorCode.SLACK_SEND_FAIL);
	}
}
