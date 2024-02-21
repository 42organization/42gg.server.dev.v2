package gg.pingpong.api.user.user.service;

import org.springframework.stereotype.Service;

import gg.pingpong.api.global.security.jwt.repository.JwtRedisRepository;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.utils.exception.user.TokenNotValidException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService {
	private final JwtRedisRepository jwtRedisRepository;
	private final AuthTokenProvider tokenProvider;

	/**
	 * Refresh 토큰으로 Access 토큰을 재발급.
	 * @throws TokenNotValidException 일치하는 Refresh 토큰 없는 경우
	 * @param refreshToken Refresh 토큰
	 * @return String Access 토큰
	 */
	public String regenerate(String refreshToken) {
		Long userId = jwtRedisRepository.getUserIdFromRefToken(refreshToken);
		if (userId == null) {
			throw new TokenNotValidException();
		}
		return tokenProvider.createToken(userId);
	}
}
