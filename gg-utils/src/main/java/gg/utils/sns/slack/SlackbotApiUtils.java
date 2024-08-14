package gg.utils.sns.slack;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import gg.utils.external.ApiUtil;
import gg.utils.sns.slack.constant.SlackConstant;
import gg.utils.sns.slack.response.ConversationResponse;
import gg.utils.sns.slack.response.SlackUserInfoResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlackbotApiUtils {

	@Value("${slack.xoxbToken}")
	private String authenticationToken;

	private final ApiUtil apiUtil;

	public String findSlackUserIdByIntraId(String intraId) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		httpHeaders.setBearerAuth(authenticationToken);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("email", convertToIntraEmail(intraId));

		SlackUserInfoResponse res = apiUtil.apiCall(
			SlackConstant.GET_USER_ID_URL.getValue(),
			SlackUserInfoResponse.class,
			httpHeaders,
			params,
			HttpMethod.POST
		);

		if (Objects.isNull(res) || Objects.isNull(res.getUser())) {
			throw new RuntimeException("슬랙 API 고장으로 인한 NULL 참조" + intraId);
		}
		return res.getUser().getId();
	}

	private String convertToIntraEmail(String intraId) {
		return intraId + SlackConstant.INTRA_EMAIL_SUFFIX;
	}

	public String createChannel(String slackUser) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(authenticationToken);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("users", slackUser);

		ConversationResponse res = apiUtil.apiCall(
			SlackConstant.GET_USER_ID_URL.getValue(),
			ConversationResponse.class,
			httpHeaders,
			params,
			HttpMethod.POST
		);

		if (Objects.isNull(res) || Objects.isNull(res.getChannel())) {
			throw new RuntimeException("슬랙 API 고장으로 인한 NULL 참조" + slackUser);
		}
		return res.getChannel().getId();
	}

	public String createGroupChannel(List<String> slackUserNames) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(authenticationToken);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("users", String.join(",", slackUserNames));

		ConversationResponse res = apiUtil.apiCall(
			SlackConstant.CONVERSATION_URL.getValue(),
			ConversationResponse.class,
			httpHeaders,
			params,
			HttpMethod.POST
		);

		if (Objects.isNull(res) || Objects.isNull(res.getChannel())) {
			throw new RuntimeException("슬랙 API 고장으로 인한 NULL 참조" + slackUserNames);
		}
		return res.getChannel().getId();
	}

	public void sendSlackMessage(String message, String channelId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(authenticationToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("channel", channelId);
		params.add("text", message);

		apiUtil.apiCall(
			SlackConstant.SEND_MESSAGE_URL.getValue(),
			String.class,
			headers,
			params,
			HttpMethod.POST
		);
	}
}
