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

    public void addRefToken(String refTokenKey, String refreshToken, long timeOut) {
        redisTemplate.opsForValue().set(refTokenKey, refreshToken, timeOut, TimeUnit.MILLISECONDS);
    }

    public String getRefToken(String refTokenKey){
        return redisTemplate.opsForValue().get(refTokenKey);
    }

    public void deleteRefToken(String refTokenKey) {
        redisTemplate.delete(refTokenKey);
    }
}
