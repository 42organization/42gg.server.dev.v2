package gg.utils.sns.slack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import gg.utils.resttemplate.ApiUtils;
import gg.utils.sns.slack.constant.SlackConstant;
import gg.utils.sns.slack.response.ConversationResponse;
import gg.utils.sns.slack.response.SlackUserInfoResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlackbotUtils {

	@Value("${slack.xoxbToken}")
	private String authenticationToken;

	private final ApiUtils apiUtils;

	public String findSlackUserIdByIntraId(String intraId) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add(HttpHeaders.AUTHORIZATION,
			SlackConstant.AUTHENTICATION_PREFIX + authenticationToken);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("email", intraId + SlackConstant.INTRA_EMAIL_SUFFIX);

		SlackUserInfoResponse res = apiUtils.apiCall(
			SlackConstant.GET_USER_ID_URL.getValue(),
			SlackUserInfoResponse.class,
			headers,
			params,
			HttpMethod.POST
		);
		if (Objects.isNull(res) || Objects.isNull(res.getUser())) {
			throw new RuntimeException("슬랙 API 고장으로 인한 NULL 참조" + intraId);
		}

		return res.getUser().getId();
	}

	public String createChannel(String slackUser) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.AUTHORIZATION,
			SlackConstant.AUTHENTICATION_PREFIX + authenticationToken);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("users", slackUser);

		ConversationResponse res = apiUtils.apiCall(
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
		httpHeaders.add(HttpHeaders.AUTHORIZATION,
			SlackConstant.AUTHENTICATION_PREFIX + authenticationToken);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("users", String.join(",", slackUserNames));

		ConversationResponse res = apiUtils.apiCall(
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
		headers.add(HttpHeaders.AUTHORIZATION,
			SlackConstant.AUTHENTICATION_PREFIX + authenticationToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("channel", channelId);
		params.add("text", message);

		apiUtils.apiCall(
			SlackConstant.SEND_MESSAGE_URL.getValue(),
			String.class,
			headers,
			params,
			HttpMethod.POST
		);
	}
}
