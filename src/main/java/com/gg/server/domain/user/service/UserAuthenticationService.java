package com.gg.server.domain.user.service;

import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.user.dto.UserJwtTokenDto;
import com.gg.server.domain.user.exception.TokenNotValidException;
import com.gg.server.global.security.config.properties.AppProperties;
import com.gg.server.global.security.jwt.repository.JwtRedisRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService {
    private final AppProperties appProperties;
    private final JwtRedisRepository jwtRedisRepository;
    private final AuthTokenProvider tokenProvider;

    public UserJwtTokenDto regenerate(String refreshToken) {
        Long userId = tokenProvider.getUserIdFormRefreshToken(refreshToken);
        if (userId == null)
            throw new TokenNotValidException();
        String refTokenKey = RedisKeyManager.getRefKey(userId);
        String redisRefToken = jwtRedisRepository.getRefToken(refTokenKey);
        if (redisRefToken == null)
            throw new TokenNotValidException();
        if (!redisRefToken.equals(refreshToken)){
            jwtRedisRepository.deleteRefToken(refTokenKey);
            throw new TokenNotValidException();
        }
        return authenticationSuccess(userId, refTokenKey);
    }

    private UserJwtTokenDto authenticationSuccess(Long userId, String refTokenKey) {
        String newRefToken = tokenProvider.refreshToken(userId);
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        jwtRedisRepository.addRefToken(refTokenKey, newRefToken, refreshTokenExpiry);
        String newAccessToken = tokenProvider.createToken(userId);
        return new UserJwtTokenDto(newAccessToken, newRefToken);
    }
}
