package com.gg.server.domain.user.service;

import org.springframework.stereotype.Service;

import com.gg.server.domain.user.exception.TokenNotValidException;
import com.gg.server.global.security.jwt.repository.JwtRedisRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService {
	private final JwtRedisRepository jwtRedisRepository;
	private final AuthTokenProvider tokenProvider;

	public String regenerate(String refreshToken) {
		Long userId = jwtRedisRepository.getUserIdFromRefToken(refreshToken);
		if (userId == null) {
			throw new TokenNotValidException();
		}
		return tokenProvider.createToken(userId);
	}
}
