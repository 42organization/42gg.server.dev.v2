package com.gg.server.domain.noti.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.data.noti.Noti;
import com.gg.server.data.user.type.SnsType;
import com.gg.server.domain.noti.dto.UserNotiDto;
import com.gg.server.domain.noti.service.sns.NotiMailSender;
import com.gg.server.domain.noti.service.sns.SlackbotService;
import com.gg.server.domain.user.dto.UserDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SnsNotiService {
	private final NotiMailSender notiMailSender;
	private final SlackbotService slackbotService;

	public SnsNotiService(NotiMailSender notiMailSender, SlackbotService slackbotService) {
		this.notiMailSender = notiMailSender;
		this.slackbotService = slackbotService;
	}

	/**
	 * 유저가 설정해둔 알림 옵션(email, slack, both, none)에 따라 알림을 전송합니다.
	 * UserNotiDto 이용
	 * @param noti 알림
	 * @param user 유저
	 */
	@Transactional(readOnly = true)
	public void sendSnsNotification(Noti noti, UserNotiDto user) {
		log.info("Send Sns Noti");
		SnsType userSnsNotiOpt = user.getSnsNotiOpt();
		if (userSnsNotiOpt == SnsType.NONE) {
			return;
		}
		if (userSnsNotiOpt == SnsType.EMAIL) {
			notiMailSender.send(user, noti);
		} else if (userSnsNotiOpt == SnsType.SLACK) {
			slackbotService.send(user, noti);
		} else if (userSnsNotiOpt == SnsType.BOTH) {
			notiMailSender.send(user, noti);
			slackbotService.send(user, noti);
		}
	}

	/**
	 * 유저가 설정해둔 알림 옵션(email, slack, both, none)에 따라 알림을 전송합니다.
	 * UserDto 이용
	 * @param noti 알림
	 * @param user 유저
	 */
	@Transactional(readOnly = true)
	public void sendSnsNotification(Noti noti, UserDto user) {
		log.info("Send Sns Noti");
		SnsType userSnsNotiOpt = user.getSnsNotiOpt();
		if (userSnsNotiOpt == SnsType.NONE) {
			return;
		}
		if (userSnsNotiOpt == SnsType.EMAIL) {
			notiMailSender.send(user, noti);
		} else if (userSnsNotiOpt == SnsType.SLACK) {
			slackbotService.send(user, noti);
		} else if (userSnsNotiOpt == SnsType.BOTH) {
			notiMailSender.send(user, noti);
			slackbotService.send(user, noti);
		}
	}
}
