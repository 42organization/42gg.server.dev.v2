package gg.pingpong.api.user.noti.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.user.User;
import gg.pingpong.api.user.noti.service.sns.SlackPartybotService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PartyNotiService {
	private final SlackPartybotService slackPartybotService;

	public PartyNotiService(SlackPartybotService slackPartybotService) {
		this.slackPartybotService = slackPartybotService;
	}

	@Transactional(readOnly = true)
	public void sendPartyNotifications(List<User> users) {
		slackPartybotService.partySend(users);
	}
}
