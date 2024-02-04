package com.gg.server.domain.noti.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.dto.UserNotiDto;
import com.gg.server.domain.noti.service.sns.NotiMailSender;
import com.gg.server.domain.noti.service.sns.SlackbotService;
import com.gg.server.domain.team.dto.GameUser;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("SnsNotiServiceTest")
class SnsNotiServiceUnitTest {
	@Mock
	NotiMailSender notiMailSender;
	@Mock
	SlackbotService slackbotService;
	@InjectMocks
	SnsNotiService snsNotiService;

	@Nested
	@DisplayName("sendSnsNotification 메서드 Test")
	class SendSnsNotificationTest {
		@Test
		@DisplayName("Email")
		void sendSnsNotificationWithEmail() {
			//given
			Noti noti = new Noti();
			UserDto userDto = mock(UserDto.class);
			when(userDto.getSnsNotiOpt()).thenReturn(SnsType.EMAIL);
			//when
			snsNotiService.sendSnsNotification(noti, userDto);
			//then
			verify(slackbotService, never()).send(any(UserDto.class), any(Noti.class));
			verify(notiMailSender, times(1)).send(any(UserDto.class), any(Noti.class));
		}

		@Test
		@DisplayName("Slack")
		void sendSnsNotificationWithSlack() {
			//given
			Noti noti = new Noti();
			UserDto userDto = mock(UserDto.class);
			when(userDto.getSnsNotiOpt()).thenReturn(SnsType.SLACK);
			//when
			snsNotiService.sendSnsNotification(noti, userDto);
			//then
			verify(notiMailSender, never()).send(any(UserDto.class), any(Noti.class));
			verify(slackbotService, times(1)).send(any(UserDto.class), any(Noti.class));
		}

		@Test
		@DisplayName("Both")
		void sendSnsNotificationWithBoth() {
			//given
			Noti noti = new Noti();
			UserDto userDto = mock(UserDto.class);
			when(userDto.getSnsNotiOpt()).thenReturn(SnsType.BOTH);
			//when
			snsNotiService.sendSnsNotification(noti, userDto);
			//then
			verify(notiMailSender, times(1)).send(any(UserDto.class), any(Noti.class));
			verify(slackbotService, times(1)).send(any(UserDto.class), any(Noti.class));
		}

		@Test
		@DisplayName("None")
		void sendSnsNotificationWithNone() {
			//given
			Noti noti = new Noti();
			UserDto userDto = mock(UserDto.class);
			when(userDto.getSnsNotiOpt()).thenReturn(SnsType.NONE);
			//when
			snsNotiService.sendSnsNotification(noti, userDto);
			//then
			verify(notiMailSender, never()).send(any(UserDto.class), any(Noti.class));
			verify(slackbotService, never()).send(any(UserDto.class), any(Noti.class));
		}

		@Test
		@DisplayName("sendSnsNotificationWithUserNotiDto")
		void sendSnsNotificationWithUserNotiDto() {
			//given
			Noti noti = new Noti();
			GameUser gameUser = mock(GameUser.class);
			when(gameUser.getSnsNotiOpt()).thenReturn(SnsType.EMAIL);
			UserNotiDto user = new UserNotiDto(gameUser);
			//when
			snsNotiService.sendSnsNotification(noti, user);
			//then
			verify(notiMailSender, times(1)).send(user, noti);
			verify(slackbotService, never()).send(any(UserNotiDto.class), any(Noti.class));
		}
	}
}
