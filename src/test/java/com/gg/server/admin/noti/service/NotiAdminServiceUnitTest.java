package com.gg.server.admin.noti.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.gg.server.admin.noti.data.NotiAdminRepository;
import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.data.noti.Noti;
import com.gg.server.data.user.User;
import com.gg.server.domain.noti.service.SnsNotiService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("NotiAdminServiceUnitTest")
class NotiAdminServiceUnitTest {
	@Mock
	UserAdminRepository userAdminRepository;
	@Mock
	NotiAdminRepository notiAdminRepository;
	@Mock
	SnsNotiService snsNotiService;
	@Mock
	SendNotiAdminRequestDto sendNotiAdminRequestDto;
	@InjectMocks
	NotiAdminService notiAdminService;

	@Nested
	@DisplayName("sendAnnounceNotiToUser_메서드_unitTest")
	class SendAnnounceNotiToUserTest {
		@Test
		@DisplayName("성공")
		void success() {
			//given
			given(userAdminRepository.findByIntraId(any(String.class))).willReturn(Optional.of(mock(User.class)));
			given(notiAdminRepository.save(any(Noti.class))).willReturn(new Noti());
			willDoNothing().given(snsNotiService).sendSnsNotification(any(Noti.class), any(UserDto.class));

			//when
			notiAdminService.sendAnnounceNotiToUser(new SendNotiAdminRequestDto("TestUser", "Message"));

			//then
			verify(userAdminRepository, times(1)).findByIntraId(any(String.class));
			verify(notiAdminRepository, times(1)).save(any(Noti.class));
			verify(snsNotiService, times(1)).sendSnsNotification(any(Noti.class), any(UserDto.class));
		}

		@Test
		@DisplayName("user_not_found")
		void userNotFound() {
			//given
			SendNotiAdminRequestDto requestDto = new SendNotiAdminRequestDto("testIntraId", "TestMessage");
			when(userAdminRepository.findByIntraId(any(String.class))).thenThrow(new UserNotFoundException());

			//when, then
			assertThatThrownBy(() -> notiAdminService.sendAnnounceNotiToUser(requestDto))
				.isInstanceOf(UserNotFoundException.class);
			verify(notiAdminRepository, never()).save(any(Noti.class));
			verify(snsNotiService, never()).sendSnsNotification(any(Noti.class), any(UserDto.class));
		}
	}

	@Nested
	@DisplayName("getAllNoti_메서드_unitTest")
	class GetAllNotiTest {
		@Test
		@DisplayName("성공")
		void success() {
			//given
			given(notiAdminRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(Collections.emptyList()));

			//when
			notiAdminService.getAllNoti(mock(org.springframework.data.domain.Pageable.class));

			//then
			verify(notiAdminRepository, times(1)).findAll(any(Pageable.class));
		}
	}

	@Nested
	@DisplayName("getFilteredNotifications_메서드_unitTest")
	class GetFilteredNotificationsTest {
		@Test
		@DisplayName("성공")
		void success() {
			//given
			given(notiAdminRepository.findNotisByUserIntraId(any(org.springframework.data.domain.Pageable.class),
				any(String.class)))
				.willReturn(new PageImpl<>(Collections.emptyList()));

			//when
			notiAdminService.getFilteredNotifications(PageRequest.of(1, 1), "TestUser");

			//then
			verify(notiAdminRepository, times(1)).findNotisByUserIntraId(
				any(org.springframework.data.domain.Pageable.class), any(String.class));
		}
	}
}
