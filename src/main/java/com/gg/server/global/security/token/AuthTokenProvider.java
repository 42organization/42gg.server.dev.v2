package com.gg.server.global.security.token;

import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.global.security.domain.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.*;

@Slf4j
@Component
public class AuthTokenProvider {
    private Key key;
    @Value("${jwt.secret}")
    private String jwtSecret;
    public static final String USER_ID = "userId";
    private final UserRepository userRepository;

    public AuthTokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void setKey(){
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public AuthToken createAuthToken(Integer userId, String intraId, Date expiry) {
        return new AuthToken(userId, intraId,  expiry, key);
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(token, key);
    }

    public Authentication getAuthentication(AuthToken authToken) {
        Claims claims = authToken.getTokenClaims();
        String userID = claims.get(USER_ID, String.class);
        User findUser = userRepository.findById(Integer.valueOf(userID)).get();
        Collection<? extends GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority(findUser.getRoleType().toString()));

        return new UsernamePasswordAuthenticationToken(UserPrincipal.create(findUser), authToken, authorities);
    }
}
