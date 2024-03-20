package gg.pingpong.api.admin.store.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import gg.admin.repo.store.CoinPolicyAdminRepository;
import gg.admin.repo.user.UserAdminRepository;
import gg.auth.UserDto;
import gg.data.store.CoinPolicy;
import gg.data.user.User;
import gg.pingpong.api.admin.store.controller.response.CoinPolicyAdminListResponseDto;
import gg.pingpong.api.admin.store.dto.CoinPolicyAdminAddDto;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.user.UserNotFoundException;

@UnitTest
@ExtendWith(MockitoExtension.class)
@DisplayName("CoinPolicyAdminServiceUnitTest")
class CoinPolicyAdminServiceUnitTest {
	@Mock
	CoinPolicyAdminRepository coinPolicyAdminRepository;
	@Mock
	UserAdminRepository userAdminRepository;
	@InjectMocks
	CoinPolicyAdminService coinPolicyAdminService;

	@Nested
	@DisplayName("findAllCoinPolicy 메서드 unitTest")
	class FindAllCoinPolicyTest {
		@Test
		@DisplayName("success")
		void success() {
			//given
			Pageable pageable = PageRequest.of(0, 10);
			CoinPolicy coinPolicy = mock(CoinPolicy.class);
			Page<CoinPolicy> coinPolicyPage = new PageImpl<>(Collections.singletonList(coinPolicy));
			when(coinPolicy.getUser()).thenReturn(mock(User.class));
			when(coinPolicyAdminRepository.findAll(pageable)).thenReturn(coinPolicyPage);
			//when
			CoinPolicyAdminListResponseDto responseDto = coinPolicyAdminService.findAllCoinPolicy(pageable);
			//then
			assertNotNull(responseDto);
			verify(coinPolicyAdminRepository).findAll(pageable);
		}

		@Test
		@DisplayName("UserNotFoundException")
		void userNotFoundException() {
			//given
			Pageable pageable = PageRequest.of(0, 10);
			CoinPolicy coinPolicy = mock(CoinPolicy.class);
			Page<CoinPolicy> coinPolicyPage = new PageImpl<>(Collections.singletonList(coinPolicy));
			when(coinPolicyAdminRepository.findAll(pageable)).thenReturn(coinPolicyPage);
			//when
			assertThatThrownBy(() -> coinPolicyAdminService.findAllCoinPolicy(pageable))
				.isInstanceOf(UserNotFoundException.class);
			//then
			verify(coinPolicyAdminRepository).findAll(pageable);
		}
	}

	@Nested
	@DisplayName("addCoinPolicy 메서드 unitTest")
	class AddCoinPolicyTest {
		@Test
		@DisplayName("success")
		void success() {
			//given
			String userId = "testId";
			UserDto userDto = mock(UserDto.class);
			when(userDto.getIntraId()).thenReturn(userId);
			given(userAdminRepository.findByIntraId(any(String.class))).willReturn(Optional.of(mock(User.class)));
			given(coinPolicyAdminRepository.save(any(CoinPolicy.class))).willReturn(new CoinPolicy());
			//when
			coinPolicyAdminService.addCoinPolicy(userDto, new CoinPolicyAdminAddDto());
			//then
			verify(userAdminRepository).findByIntraId(any(String.class));
			verify(coinPolicyAdminRepository).save(any(CoinPolicy.class));
		}

		@Test
		@DisplayName("UserNotFoundException")
		void userNotFoundException() {
			//given
			String userId = "testId";
			UserDto userDto = mock(UserDto.class);
			when(userDto.getIntraId()).thenReturn(userId);
			given(userAdminRepository.findByIntraId(any(String.class))).willReturn(Optional.empty());
			//when, then
			assertThatThrownBy(() -> coinPolicyAdminService.addCoinPolicy(userDto, new CoinPolicyAdminAddDto()))
				.isInstanceOf(UserNotFoundException.class);
			verify(userAdminRepository).findByIntraId(any(String.class));
			verify(coinPolicyAdminRepository, never()).save(any(CoinPolicy.class));
		}
	}
}

