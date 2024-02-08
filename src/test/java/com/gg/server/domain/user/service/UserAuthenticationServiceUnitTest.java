package com.gg.server.domain.user.service;

import static org.mockito.Mockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gg.server.domain.user.exception.TokenNotValidException;
import com.gg.server.global.security.jwt.repository.JwtRedisRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.annotation.UnitTest;

@UnitTest
@ExtendWith(MockitoExtension.class)
class UserAuthenticationServiceUnitTest {
	@Mock
	JwtRedisRepository jwtRedisRepository;

	@Mock
	AuthTokenProvider tokenProvider;

	@InjectMocks
	UserAuthenticationService userAuthenticationService;

	@Nested
	@DisplayName("regenerate")
	class Regenerate {
		@Test
		@DisplayName("새로운 access token 반환")
		void generateNewAccessToken() {
			//Arrange
			String refreshToken = "valid token";
			String newAccessToken = "new_access_token";
			Long userId = 1L;

			when(jwtRedisRepository.getUserIdFromRefToken(refreshToken)).thenReturn(userId);
			when(tokenProvider.createToken(userId)).thenReturn(newAccessToken);

			//Act
			String accessToken = userAuthenticationService.regenerate(refreshToken);

			//Assert
			Assertions.assertThat(accessToken).isEqualTo(newAccessToken);
		}

		@Test
		@DisplayName("유효하지 않은 refreshToken TokenNotValidException 발생")
		void refreshTokenInvalid() {
			//Arrange
			String refreshToken = "invalid token";

			when(jwtRedisRepository.getUserIdFromRefToken(refreshToken)).thenReturn(null);

			//Act, Assert
			Assertions.assertThatThrownBy(() -> userAuthenticationService.regenerate(refreshToken))
				.isInstanceOf(TokenNotValidException.class);
		}
	}
}
