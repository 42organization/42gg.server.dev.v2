package gg.pingpong.api.user.noti.service.sns;

import static org.mockito.Mockito.*;

import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import gg.data.noti.Noti;
import gg.pingpong.api.global.utils.AsyncMailSender;
import gg.pingpong.api.user.noti.dto.UserNotiDto;
import gg.pingpong.api.user.noti.service.NotiService;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.repo.game.out.GameUser;
import gg.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("NotiMailSenderUnitTest")
class NotiMailSenderUnitTest {
	@Mock
	JavaMailSender javaMailSender;
	@Mock
	AsyncMailSender asyncMailSender;
	@Mock
	NotiService notiService;
	@InjectMocks
	NotiMailSender notiMailSender;

	@Test
	@DisplayName("UserNotiDto를 이용하여 유저 이메일로 메일 보내기")
	void sendToUserEmailByUserNotiDto() {
		// given
		GameUser gameUser = mock(GameUser.class);
		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(gameUser.getEmail()).thenReturn("testEmail");
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		when(notiService.getMessage(any(Noti.class))).thenReturn("Test message");
		// when
		notiMailSender.send(new UserNotiDto(gameUser), new Noti());
		// then
		verify(javaMailSender).createMimeMessage();
		verify(asyncMailSender).send(mimeMessage);
	}

	@Test
	@DisplayName("UserDto를 이용하여 유저 이메일로 메일 보내기")
	void sendToUserEmailByUserDto() {
		// given
		UserDto userDto = mock(UserDto.class);
		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(userDto.getEMail()).thenReturn("testEmail");
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		when(notiService.getMessage(any(Noti.class))).thenReturn("Test message");
		// when
		notiMailSender.send(userDto, new Noti());
		// then
		verify(javaMailSender).createMimeMessage();
		verify(asyncMailSender).send(mimeMessage);
	}
}
