package com.gg.server.global.security;

import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.global.security.config.properties.AppProperties;
import com.gg.server.global.security.domain.UserPrincipal;
import com.gg.server.global.security.jwt.Token;
import com.gg.server.global.security.repository.UserRefreshTokenRepository;
import com.gg.server.global.security.service.JwtTokenService;
import com.gg.server.global.security.token.AuthToken;
import com.gg.server.global.security.token.AuthTokenProvider;
import com.gg.server.global.utils.HeaderUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static com.gg.server.global.security.token.AuthTokenProvider.USER_ID;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)  throws ServletException, IOException {
        String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(tokenHeader) || !tokenHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String tokenStr = HeaderUtil.getAccessToken(request);
        AuthToken token = tokenProvider.convertAuthToken(tokenStr);
        if (!token.validate()) {
            filterChain.doFilter(request, response);
            return;
        }
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

}
