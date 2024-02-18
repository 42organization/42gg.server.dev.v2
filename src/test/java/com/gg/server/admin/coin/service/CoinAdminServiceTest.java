package com.gg.server.admin.coin.service;

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

import com.gg.server.admin.coin.dto.CoinUpdateRequestDto;
import com.gg.server.data.store.CoinHistory;
import com.gg.server.data.user.User;
import com.gg.server.domain.coin.service.CoinHistoryService;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.exception.UserNotFoundException;
import com.gg.server.utils.annotation.UnitTest;

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
