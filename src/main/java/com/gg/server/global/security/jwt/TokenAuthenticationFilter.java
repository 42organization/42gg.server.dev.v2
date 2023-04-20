package com.gg.server.global.security.jwt;

import com.gg.server.global.security.service.CustomUserDetailsService;
import com.gg.server.global.utils.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private final CustomUserDetailsService customUserDetailsService;

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
            System.out.println("token: " + tokenHeader);
            String tokenStr = HeaderUtil.getAccessToken(request);//.substring(7);
            if (tokenProvider.getTokenClaims(tokenStr) == null) {
                throw new Exception("token not valid");
            }
            Long userId = tokenProvider.getUserIdFromToken(tokenStr);
            UserDetails userDetails = customUserDetailsService.loadUserById(userId);
            OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken((OAuth2User) userDetails, userDetails.getAuthorities(), "42");
            System.out.println(userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            log.error("Security Context에서 사용자 인증을 설정할 수 없습니다.", e);
        }
        filterChain.doFilter(request, response);
    }

}
