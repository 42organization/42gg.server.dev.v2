package gg.pingpong.api.user.noti.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.user.User;
import gg.utils.sns.slack.AsyncSlackMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyNotiService {

	private static final String PARTY_MESSAGE = "파티요정🧚으로부터 편지가 도착했습니다.\n"
		+ "장소 및 시간을 상호 협의해서 진행해주세요.\n"
		+ "파티원이 연락두절이라면 $$마지 못해 신고$$ ----> https://42gg.kr";

	private final AsyncSlackMessageSender asyncSlackMessageSender;

	@Transactional(readOnly = true)
	public void sendPartyNotifications(List<User> users) {
		List<String> intraUserNames = users.stream().map(User::getIntraId).collect(Collectors.toList());
		asyncSlackMessageSender.sendGroup(intraUserNames, PARTY_MESSAGE);
	}
}
