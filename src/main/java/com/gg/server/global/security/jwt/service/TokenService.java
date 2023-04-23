package com.gg.server.global.security.jwt.service;

import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.global.security.jwt.entity.RefreshToken;
import com.gg.server.global.security.jwt.exception.TokenNotValidException;
import com.gg.server.global.security.jwt.repository.RefreshTokenRepository;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final RefreshTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final AuthTokenProvider tokenProvider;

    public void saveRefreshToken(Long userId, String refreshToken) {
        Optional<RefreshToken> optionalRefreshToken = tokenRepository.findByUserId(userId);
        if (optionalRefreshToken.isEmpty()) {
            Optional<User> findUser = userRepository.findById(userId);
            RefreshToken tokenEntity = new RefreshToken(findUser.get(), refreshToken);
            tokenRepository.save(tokenEntity);
        } else {
            optionalRefreshToken.get().setRefreshToken(refreshToken);
        }
    }

    public String generateNewAccessToken(String refreshToken) {
        if (tokenProvider.getTokenClaims(refreshToken) != null){
            Optional<RefreshToken> optionalRefreshToken = tokenRepository.findByRefreshToken(refreshToken);
            if (optionalRefreshToken.isEmpty())
                throw new TokenNotValidException();
            String accessToken = tokenProvider.createToken(optionalRefreshToken.get().getUser().getId());
            return accessToken;
        }
        throw new TokenNotValidException();
    }
}
