package com.gg.server.global.security.service;

import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.global.security.config.properties.AppProperties;
import com.gg.server.global.security.jwt.Token;
import com.gg.server.global.security.repository.UserRefreshTokenRepository;
import com.gg.server.global.security.token.AuthToken;
import com.gg.server.global.security.token.AuthTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

import static com.gg.server.global.security.token.AuthTokenProvider.USER_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class JwtTokenService {

    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final AuthTokenProvider tokenProvider;
    private final AppProperties appProperties;

    public Token generateToken(Integer id, AuthToken accessToken, AuthToken refreshToken) {
        User saveUser = userRepository.findById(id).orElseThrow();
        Token token = new Token(saveUser, refreshToken.getToken(), accessToken.getToken());
        userRefreshTokenRepository.save(token);
        return token;
    }

    public void regenerateToken(AuthToken token) {
        Claims claims = token.getTokenClaims();
        String userID = claims.get(USER_ID, String.class);
        User user = userRepository.findById(Integer.valueOf(userID)).get();
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        Date now = new Date();
        AuthToken refreshToken = tokenProvider.createAuthToken(
                user.getId(),
                user.getIntraId(),
                new Date(now.getTime() + refreshTokenExpiry)
        );
        Token existToken = userRefreshTokenRepository.findByUser(user);
        existToken.setRefreshToken(refreshToken.getToken());
    }
}
