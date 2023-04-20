package com.gg.server.global.security.repository;

import com.gg.server.domain.user.User;
import com.gg.server.global.security.jwt.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<Token, Long> {
    Token findByUser(User user);
    Token findByUserAndRefreshToken(User user, String refreshToken);
    Token findByRefreshToken(String token);
}