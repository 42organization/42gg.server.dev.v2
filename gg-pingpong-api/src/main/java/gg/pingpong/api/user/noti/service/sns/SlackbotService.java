package gg.pingpong.api.user.noti.service.sns;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import gg.auth.UserDto;
import gg.data.noti.Noti;
import gg.pingpong.api.user.noti.dto.UserNotiDto;
import gg.pingpong.api.user.noti.service.NotiService;
import gg.utils.exception.noti.SlackSendException;
import gg.utils.external.ApiUtil;
import gg.utils.sns.slack.constant.SlackConstant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Component
@Slf4j
@RequiredArgsConstructor
public class SlackbotService {
	@Value("${slack.xoxbToken}")
	private String authenticationToken;

	private final NotiService notiService;
	private final ApiUtil apiUtil;

	private String getSlackUserId(String intraId) {
		String userEmail = intraId + SlackConstant.INTRA_EMAIL_SUFFIX;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBearerAuth(authenticationToken);

		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("email", userEmail);

		SlackUserInfoResponse res = apiUtil.apiCall(
			SlackConstant.GET_USER_ID_URL.getValue(),
			SlackUserInfoResponse.class,
			headers, parameters, HttpMethod.POST);

		if (res == null || res.getUser() == null) {
			throw new RuntimeException("슬랙 API 고장으로 인한 NULL 참조" + intraId);
		}

		return res.user.id;
	}

	private String getDmChannelId(String slackUserId) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(authenticationToken);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		Map<String, String> bodyMap = new HashMap<>();
		bodyMap.put("users", slackUserId);

		ConversationResponse res = apiUtil.apiCall(
			SlackConstant.CONVERSATION_URL.getValue(),
			ConversationResponse.class,
			httpHeaders, bodyMap, HttpMethod.POST);

		return res.channel.id;
	}

	@Async("asyncExecutor")
	public void send(UserNotiDto user, Noti noti) {
		log.info("slack alarm send");
		try {
			startSendNoti(user.getIntraId(), noti);
		} catch (SlackSendException e) {
			log.error("SlackSendException message = {}", e.getMessage());
		}
	}

	@Async("asyncExecutor")
	public void send(UserDto user, Noti noti) {
		log.info("slack alarm send");
		try {
			startSendNoti(user.getIntraId(), noti);
		} catch (SlackSendException e) {
			log.error("SlackSendException message = {}", e.getMessage());
		}
	}

	private void startSendNoti(String intraId, Noti noti) {
		String slackUserId = getSlackUserId(intraId);
		String slackChannelId = getDmChannelId(slackUserId);
		String message = notiService.getMessage(noti);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(authenticationToken);
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		Map<String, String> map = new HashMap<>();
		map.put("channel", slackChannelId);
		map.put("text", message);
		apiUtil.apiCall(
			SlackConstant.SEND_MESSAGE_URL.getValue(),
			String.class, httpHeaders, map, HttpMethod.POST);
	}

	@Getter
	static class ConversationResponse {
		private Boolean ok;
		private Channel channel;

		@Getter
		static class Channel {
			private String id;
		}
	}

	@Getter
	static class SlackUserInfoResponse {
		private Boolean ok;
		private SlackUser user;

		@Getter
		static class SlackUser {
			private String id;
		}
	}
}
