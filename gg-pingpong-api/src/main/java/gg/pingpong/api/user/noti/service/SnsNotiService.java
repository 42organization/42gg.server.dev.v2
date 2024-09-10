package gg.pingpong.api.user.noti.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.auth.UserDto;
import gg.data.noti.Noti;
import gg.data.user.type.SnsType;
import gg.pingpong.api.user.noti.dto.UserNotiDto;
import gg.pingpong.api.user.noti.service.sns.NotiMailSender;
import gg.pingpong.api.user.noti.service.sns.NotiSlackMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnsNotiService {

	private final NotiMailSender notiMailSender;

	private final NotiSlackMessageSender notiSlackMessageSender;

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
			notiSlackMessageSender.send(user, noti);
		} else if (userSnsNotiOpt == SnsType.BOTH) {
			notiMailSender.send(user, noti);
			notiSlackMessageSender.send(user, noti);
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
			notiSlackMessageSender.send(user, noti);
		} else if (userSnsNotiOpt == SnsType.BOTH) {
			notiMailSender.send(user, noti);
			notiSlackMessageSender.send(user, noti);
		}
	}
}
