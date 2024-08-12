package gg.utils.sns.slack.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SlackConstant {

	CONVERSATION_URL("https://slack.com/api/conversations.open"),
	SEND_MESSAGE_URL("https://slack.com/api/chat.postMessage"),
	GET_USER_ID_URL("https://slack.com/api/users.lookupByEmail"),
	INTRA_EMAIL_SUFFIX("@student.42seoul.kr");

	private final String value;
}
