package gg.utils.sns.slack;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import gg.utils.exception.noti.SlackSendException;
import gg.utils.sns.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncSlackMessageSender implements MessageSender {

	private final SlackbotApiUtils slackbotApiUtils;

	@Async("asyncExecutor")
	public void send(String intraUsername, String message) {
		log.info("slack alarm send");
		try {
			String slackUsername = slackbotApiUtils.findSlackUserIdByIntraId(intraUsername);
			String slackChannelName = slackbotApiUtils.createChannel(slackUsername);
			slackbotApiUtils.sendSlackMessage(message, slackChannelName);
		} catch (SlackSendException e) {
			log.error("SlackSendException message = {}", e.getMessage());
		}
	}

	@Async("asyncExecutor")
	public void sendGroup(List<String> intraUsernames, String message) {
		log.info("slack alarm send");
		try {
			List<String> slackUsernames = intraUsernames.stream()
				.map(slackbotApiUtils::findSlackUserIdByIntraId)
				.collect(Collectors.toList());
			String channelName = slackbotApiUtils.createGroupChannel(slackUsernames);
			slackbotApiUtils.sendSlackMessage(message, channelName);
		} catch (SlackSendException e) {
			log.error("SlackSendException message = {}", e.getMessage());
		}
	}
}
