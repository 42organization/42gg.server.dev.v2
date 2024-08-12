package gg.utils.sns;

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

import gg.utils.exception.noti.SlackSendException;
import gg.utils.resttemplate.ApiUtils;
import gg.utils.sns.slack.SlackbotUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncSlackMessageSender {

	private final SlackbotUtils slackbotUtils;

	@Async("asyncExecutor")
	public void send(String intraId, String message) {
		log.info("slack alarm send");
		try {
			String slackUserId = slackbotUtils.findSlackUserIdByIntraId(intraId);
			String slackChannelId = slackbotUtils.createChannel(slackUserId);
			slackbotUtils.sendSlackMessage(message, slackChannelId);
		} catch (SlackSendException e) {
			log.error("SlackSendException message = {}", e.getMessage());
		}
	}

	@Async("asyncExecutor")
	public void sendGroup(List<String> intraUsers, String message) {
		log.info("slack alarm send");
		try {
			List<String> slackUsers = intraUsers.stream()
				.map(slackbotUtils::findSlackUserIdByIntraId)
				.collect(Collectors.toList());
			String channelId = slackbotUtils.createGroupChannel(slackUsers);
			slackbotUtils.sendSlackMessage(message, channelId);
		} catch (SlackSendException e) {
			log.error("SlackSendException message = {}", e.getMessage());
		}
	}
}
