package com.gg.server.global.security.jwt.repository;

import com.gg.server.domain.user.exception.TokenNotValidException;
import com.gg.server.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class JwtRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void addRefToken(String refreshToken, String userId, long timeOut) {
        redisTemplate.opsForValue().set(refreshToken, userId, timeOut, TimeUnit.MILLISECONDS);
    }

    public Long getUserIdByRefToken(String refreshToken){
        String userId = redisTemplate.opsForValue().get(refreshToken);
        if (userId == null)
            throw new TokenNotValidException();
        return Long.valueOf(userId);
    }

}
