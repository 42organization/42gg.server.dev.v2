package gg.pingpong.api.user.noti.service.sns;

import static org.mockito.Mockito.*;

import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gg.auth.UserDto;
import gg.data.noti.Noti;
import gg.pingpong.api.user.noti.dto.UserNotiDto;
import gg.pingpong.api.user.noti.service.NotiService;
import gg.repo.game.out.GameUser;
import gg.utils.annotation.UnitTest;
import gg.utils.sns.MailSender;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("NotiMailSenderUnitTest")
class NotiMailSenderUnitTest {

	@Mock
	MailSender mailSender;

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
		doNothing().when(mailSender).send(anyString(), anyString(), anyString());
		when(notiService.getMessage(any(Noti.class))).thenReturn("Test message");

		// when
		notiMailSender.send(new UserNotiDto(gameUser), new Noti());

		// then
		verify(notiService).getMessage(any(Noti.class));
		verify(mailSender).send(anyString(), anyString(), anyString());
	}

	@Test
	@DisplayName("UserDto를 이용하여 유저 이메일로 메일 보내기")
	void sendToUserEmailByUserDto() {
		// given
		UserDto userDto = mock(UserDto.class);
		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(userDto.getEMail()).thenReturn("testEmail");
		doNothing().when(mailSender).send(anyString(), anyString(), anyString());
		when(notiService.getMessage(any(Noti.class))).thenReturn("Test message");
		// when
		notiMailSender.send(userDto, new Noti());

		// then
		verify(notiService).getMessage(any(Noti.class));
		verify(mailSender).send(anyString(), anyString(), anyString());
	}
}
