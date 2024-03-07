package gg.pingpong.api.global.security.jwt.utils;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import gg.auth.utils.AuthTokenProvider;
import gg.auth.utils.HeaderUtil;
import gg.pingpong.api.global.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
	private final AuthTokenProvider tokenProvider;
	private final CustomUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (isEmpty(tokenHeader) || !tokenHeader.startsWith("Bearer ")) {
				filterChain.doFilter(request, response);
				return;
			}
			OAuth2AuthenticationToken authentication = validate(request);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
			log.error("Security Context에서 사용자 인증을 설정할 수 없습니다.", e);
		}
		filterChain.doFilter(request, response);
	}

	private OAuth2AuthenticationToken validate(HttpServletRequest request) {
		String accessToken = HeaderUtil.getAccessToken(request);
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		//access token 검증
		if (userId != null) {
			UserDetails userDetails = customUserDetailsService.loadUserById(userId);
			return new OAuth2AuthenticationToken((OAuth2User)userDetails, userDetails.getAuthorities(), "42");
		}
		throw new RuntimeException("token not validated");
	}

}
