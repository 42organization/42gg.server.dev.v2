package com.gg.server.admin.noti.service;

import com.gg.server.admin.noti.data.NotiAdminRepository;
import com.gg.server.admin.noti.dto.SendNotiAdminRequestDto;
import com.gg.server.admin.user.data.UserAdminRepository;
import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.service.SnsNotiService;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.utils.annotation.UnitTest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

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
        void UserNotFound() {
            //when, then
            assertThatThrownBy(() -> notiAdminService.sendAnnounceNotiToUser(mock(SendNotiAdminRequestDto.class)))
                    .isInstanceOf(UserNotFoundException.class);
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
            given(notiAdminRepository.findNotisByUserIntraId(any(org.springframework.data.domain.Pageable.class), any(String.class)))
                    .willReturn(new PageImpl<>(Collections.emptyList()));

            //when
            notiAdminService.getFilteredNotifications(PageRequest.of(1, 1), "TestUser");

            //then
            verify(notiAdminRepository, times(1)).findNotisByUserIntraId(any(org.springframework.data.domain.Pageable.class), any(String.class));
        }
    }
}