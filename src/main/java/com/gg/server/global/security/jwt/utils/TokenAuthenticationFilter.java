package com.gg.server.global.security.jwt.utils;

import com.gg.server.global.security.config.properties.AppProperties;
import com.gg.server.global.security.cookie.CookieUtil;
import com.gg.server.global.security.service.CustomUserDetailsService;
import com.gg.server.global.utils.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final AppProperties appProperties;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)  throws ServletException, IOException {
        try {
            String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (isEmpty(tokenHeader) || !tokenHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            OAuth2AuthenticationToken authentication = validate(request, response);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("Security Context에서 사용자 인증을 설정할 수 없습니다.", e);
        }
        filterChain.doFilter(request, response);
    }

    private OAuth2AuthenticationToken validate(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = HeaderUtil.getAccessToken(request);

        //access token 검증
        if (tokenProvider.getTokenClaims(accessToken) != null){
            Long userId = tokenProvider.getUserIdFromToken(accessToken);
            UserDetails userDetails = customUserDetailsService.loadUserById(userId);
            return new OAuth2AuthenticationToken((OAuth2User) userDetails, userDetails.getAuthorities(), "42");
        }
        throw new RuntimeException("token not validated");
    }

}
