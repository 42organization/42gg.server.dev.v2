package gg.pingpong.api.admin.coin.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gg.pingpong.api.admin.store.controller.request.CoinUpdateRequestDto;
import gg.pingpong.api.admin.store.service.CoinAdminService;
import gg.pingpong.api.user.store.service.CoinHistoryService;
import gg.pingpong.data.store.CoinHistory;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.annotation.UnitTest;
import gg.pingpong.utils.exception.user.UserNotFoundException;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("CoinAdminServiceTest")
class CoinAdminServiceTest {
	@Mock
	UserRepository userRepository;
	@Mock
	CoinHistoryService coinHistoryService;
	@InjectMocks
	CoinAdminService coinAdminService;

	@Nested
	@DisplayName("updateUserCoin 메서드 unitTest")
	class UpdateUserCoinTest {
		@Test
		@DisplayName("성공")
		void success() {
			//given
			CoinUpdateRequestDto requestDto = new CoinUpdateRequestDto("testId", 10, "test");
			User user = mock(User.class);
			given(userRepository.findByIntraId(any(String.class))).willReturn(Optional.of(user));
			//when
			coinAdminService.updateUserCoin(requestDto);
			//then
			verify(user).addGgCoin(requestDto.getChange());
			verify(coinHistoryService).addCoinHistory(any(CoinHistory.class));
		}

		@Test
		@DisplayName("UserNotFound")
		void userNotFound() {
			//given
			CoinUpdateRequestDto requestDto = new CoinUpdateRequestDto("testId", 10, "test");
			given(userRepository.findByIntraId(requestDto.getIntraId())).willReturn(Optional.empty());
			//when, then
			Assertions.assertThatThrownBy(() -> coinAdminService.updateUserCoin(requestDto))
				.isInstanceOf(UserNotFoundException.class);
		}
	}
}
