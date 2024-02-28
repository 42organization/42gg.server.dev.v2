package gg.pingpong.api.user.noti.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.transaction.Transactional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import gg.data.noti.Noti;
import gg.data.noti.type.NotiType;
import gg.data.user.User;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.repo.noti.NotiRepository;
import gg.repo.user.UserRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@IntegrationTest
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
	public void userMatingNotSend() throws Exception {
		//given
		String accessToken = testDataUtils.getLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.getById(userId);
		UserDto userDto = UserDto.from(user);
		LocalDateTime now = LocalDateTime.now();
		String expectedMatchedNotiMessage = now.format(DateTimeFormatter.ofPattern("HH:mm")) + "에 신청한 매칭이 성사되었습니다.";
		String expectedMatchCancelNotiMessage =
			now.format(DateTimeFormatter.ofPattern("HH:mm")) + "에 신청한 매칭이 상대에 의해 취소되었습니다.";

		//when
		notiService.createMatched(user, now);
		notiService.createMatchCancel(user, now);
		List<Noti> actureNotiList = notiRepository.findAllByUser(user);

		//then
		Assertions.assertThat(actureNotiList.size()).isEqualTo(2);
		for (Noti noti : actureNotiList) {
			if (noti.getType() == NotiType.MATCHED) {
				Assertions.assertThat(noti.getMessage()).isEqualTo(expectedMatchedNotiMessage);
				log.info("Matched Message : " + noti.getMessage());
			} else if (noti.getType() == NotiType.CANCELEDBYMAN) {
				Assertions.assertThat(noti.getMessage()).isEqualTo(expectedMatchCancelNotiMessage);
				log.info("MatchCancel Message : " + noti.getMessage());
			}
			Assertions.assertThat(noti.getIsChecked()).isFalse();
		}
	}
}
