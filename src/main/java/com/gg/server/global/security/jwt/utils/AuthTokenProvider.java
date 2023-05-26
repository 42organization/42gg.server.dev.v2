package com.gg.server.global.security.jwt.utils;

import com.gg.server.global.security.config.properties.AppProperties;
import com.gg.server.global.security.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Slf4j
@Component
public class AuthTokenProvider {

    private AppProperties appProperties;
    private final Key key;

    public AuthTokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
        key = Keys.hmacShaKeyFor(appProperties.getAuth().getTokenSecret().getBytes());
        log.info(key.getAlgorithm());
    }
    public String refreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() +
                appProperties.getAuth().getRefreshTokenExpiry());
        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String createToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() +
                appProperties.getAuth().getTokenExpiry());
        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public Claims getTokenClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null;
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getTokenClaims(token);
        return Long.parseLong(claims.getSubject());
    }

}
