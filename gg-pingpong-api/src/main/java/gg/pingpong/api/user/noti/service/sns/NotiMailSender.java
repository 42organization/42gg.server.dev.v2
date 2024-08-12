package gg.pingpong.api.user.noti.service.sns;

import org.springframework.stereotype.Component;

import gg.auth.UserDto;
import gg.data.noti.Noti;
import gg.pingpong.api.user.noti.dto.UserNotiDto;
import gg.pingpong.api.user.noti.service.NotiService;
import gg.utils.sns.MailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotiMailSender {

	private final NotiService notiService;

	private final MailSender mailSender;

	private static final String SUBJECT = "핑퐁요정🧚으로부터 도착한 편지";

	/**
	 * 알림을 전송합니다.
	 * UserNotiDto 이용
	 * @param user 유저
	 * @param noti 알림
	 */
	public void send(UserNotiDto user, Noti noti) {
		String message = notiService.getMessage(noti);
		mailSender.send(SUBJECT, user.getEmail(), message);
	}

	/**
	 * 알림을 전송합니다.
	 * UserDto 이용
	 * @param user 유저
	 * @param noti 알림
	 */
	public void send(UserDto user, Noti noti) {
		String message = notiService.getMessage(noti);
		mailSender.send(SUBJECT, user.getEMail(),message);
	}
}
