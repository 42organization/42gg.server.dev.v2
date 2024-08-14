package gg.pingpong.api.user.noti.service.sns;

import org.springframework.stereotype.Component;

import gg.auth.UserDto;
import gg.data.noti.Noti;
import gg.pingpong.api.user.noti.dto.UserNotiDto;
import gg.pingpong.api.user.noti.service.NotiService;
import gg.utils.sns.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotiSlackMessageSender {

	private final NotiService notiService;

	private final MessageSender messageSender;

	public void send(UserDto user, Noti noti) {
		String message = notiService.getMessage(noti);
		messageSender.send(user.getIntraId(), message);
	}

	public void send(UserNotiDto user, Noti noti) {
		String message = notiService.getMessage(noti);
		messageSender.send(user.getIntraId(), message);
	}
}
