package gg.utils.sns.slack.response;

import lombok.Getter;

@Getter
public class SlackUserInfoResponse {

	private Boolean ok;

	private SlackUser user;
}
