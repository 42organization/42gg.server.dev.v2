package com.gg.server.domain.user.service;

import com.gg.server.global.security.jwt.exception.TokenNotValidException;
import com.gg.server.global.security.jwt.repository.JwtRedisRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtRedisRepository jwtRedisRepository;
    private final AuthTokenProvider tokenProvider;

    public String regenerate(String refreshToken) {
        Long userId = jwtRedisRepository.getUserIdByRefToken(refreshToken);
        if (tokenProvider.getTokenClaims(refreshToken) == null)
            throw new TokenNotValidException();
        return tokenProvider.createToken(userId);
    }
}
