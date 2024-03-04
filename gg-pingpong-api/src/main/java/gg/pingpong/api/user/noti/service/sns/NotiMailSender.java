package gg.pingpong.api.user.noti.service.sns;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import gg.data.noti.Noti;
import gg.pingpong.api.global.utils.AsyncMailSender;
import gg.pingpong.api.user.noti.dto.UserNotiDto;
import gg.pingpong.api.user.noti.service.NotiService;
import gg.auth.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotiMailSender {
	private final JavaMailSender javaMailSender;
	private final AsyncMailSender asyncMailSender;
	private final NotiService notiService;

	/**
	 * 알림을 전송합니다.
	 * UserNotiDto 이용
	 * @param user 유저
	 * @param noti 알림
	 */
	public void send(UserNotiDto user, Noti noti) {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setSubject("핑퐁요정🧚으로부터 도착한 편지");
			log.info(user.getEmail());
			helper.setTo(user.getEmail());
			helper.setText(notiService.getMessage(noti));
		} catch (MessagingException e) {
			log.error("MessagingException message = {}", e.getMessage());
		}
		log.info("Email send {}", user.getUserId());
		asyncMailSender.send(message);
	}

	/**
	 * 알림을 전송합니다.
	 * UserDto 이용
	 * @param user 유저
	 * @param noti 알림
	 */
	public void send(UserDto user, Noti noti) {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setSubject("핑퐁요정🧚으로부터 도착한 편지");
			log.info(user.getEMail());
			helper.setTo(user.getEMail());
			helper.setText(notiService.getMessage(noti));
		} catch (MessagingException e) {
			log.error("MessagingException message = {}", e.getMessage());
		}
		log.info("Email send {}", user.getId());
		asyncMailSender.send(message);
	}
}
