package gg.pingpong.api.user.noti.service.sns;

import static gg.pingpong.api.user.noti.service.sns.SlackbotUtils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import gg.data.user.User;
import gg.pingpong.api.global.utils.external.ApiUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SlackPartybotService {
	@Value("${slack.xoxbToken}")
	private String authenticationToken;

	private final ApiUtil apiUtil;

	private String getSlackUserId(String intraId) {
		String userEmail = intraId + intraEmailSuffix;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add(HttpHeaders.AUTHORIZATION, authenticationPrefix + authenticationToken);

		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("email", userEmail);

		SlackUserInfoRes res = apiUtil.apiCall(userIdGetUrl, SlackUserInfoRes.class,
			headers, parameters, HttpMethod.POST);

		if (res == null || res.getUser() == null) {
			throw new RuntimeException("슬랙 API 고장으로 인한 NULL 참조" + intraId);
		}

		SlackPartybotService.SlackUserInfoRes res = apiUtil.apiCall(userIdGetUrl, SlackUserInfoRes.class,
			headers, parameters, HttpMethod.POST);
		return res.user.id;
	}

	private String createGroupChannelId(List<String> slackUserIds) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.AUTHORIZATION, authenticationPrefix + authenticationToken);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		Map<String, String> bodyMap = new HashMap<>();
		bodyMap.put("users", String.join(",", slackUserIds));

		ConversationRes res = apiUtil.apiCall(conversationsUrl, ConversationRes.class,
			httpHeaders, bodyMap, HttpMethod.POST);

		return res.channel.id;
	}

	@Async("asyncExecutor")
	public void partySend(List<User> users) {
		List<String> slackUserIds = users.stream()
			.map(User::getIntraId)
			.map(this::getSlackUserId)
			.collect(Collectors.toList());

		String slackChannelId = createGroupChannelId(slackUserIds);
		sendGroupMessage(slackChannelId, "파티요정🧚으로부터 편지가 도착했습니다."
			+ "\n장소 및 시간을 상호 협의해서 진행해주세요."
			+ "\n파티원이 연락두절이라면 $$마지 못해 신고$$ ----> https://42gg.kr");
	}

	private void sendGroupMessage(String channelId, String message) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, authenticationPrefix + authenticationToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, String> bodyMap = new HashMap<>();
		bodyMap.put("channel", channelId);
		bodyMap.put("text", message);

		apiUtil.apiCall(sendMessageUrl, String.class, headers, bodyMap, HttpMethod.POST);
	}

	@Getter
	static class ConversationRes {
		private Boolean ok;
		private Channel channel;

		@Getter
		static class Channel {
			private String id;
		}
	}

	@Getter
	static class SlackUserInfoRes {
		private Boolean ok;
		private SlackUser user;

		@Getter
		static class SlackUser {
			private String id;
		}
	}
}
