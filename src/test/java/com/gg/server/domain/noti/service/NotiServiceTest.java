package com.gg.server.domain.noti.service;

import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.data.NotiRepository;
import com.gg.server.domain.noti.type.NotiType;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
@SpringBootTest
@Slf4j
class NotiServiceTest {

    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    AuthTokenProvider tokenProvider;
    @Autowired
    NotiService notiService;
    @Autowired
    NotiRepository notiRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("NotiService 유저매칭알림 전송 테스트")
    @Transactional
    public void 유저매칭알림전송테스트() throws Exception {
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        User user = userRepository.getById(userId);
        UserDto userDto = UserDto.from(user);
        LocalDateTime now = LocalDateTime.now();
        String expectedMatchedNotiMessage = now.format(DateTimeFormatter.ofPattern("HH:mm")) + "에 신청한 매칭이 성사되었습니다.";
        String expectedMatchCancelNotiMessage = now.format(DateTimeFormatter.ofPattern("HH:mm")) + "에 신청한 매칭이 상대에 의해 취소되었습니다.";

        //when
        notiService.createMatched(userDto, now);
        notiService.createMatchCancel(userDto, now);
        List<Noti> actureNotiList = notiRepository.findAllByUser(user);

        //then
        Assertions.assertThat(actureNotiList.size()).isEqualTo(2);
        for (Noti noti: actureNotiList) {
            if (noti.getType() == NotiType.MATCHED) {
                Assertions.assertThat(noti.getMessage()).isEqualTo(expectedMatchedNotiMessage);
                log.info("Matched Message : " + noti.getMessage());
            }
            else if (noti.getType() == NotiType.CANCELEDBYMAN) {
                Assertions.assertThat(noti.getMessage()).isEqualTo(expectedMatchCancelNotiMessage);
                log.info("MatchCancel Message : " + noti.getMessage());
            }
            Assertions.assertThat(noti.getIsChecked()).isFalse();
        }
    }
}