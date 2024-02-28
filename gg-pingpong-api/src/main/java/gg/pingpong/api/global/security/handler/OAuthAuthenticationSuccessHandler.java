package gg.pingpong.api.global.security.handler;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import gg.data.user.User;
import gg.data.user.type.RoleType;
import gg.pingpong.api.global.security.UserPrincipal;
import gg.pingpong.api.global.security.config.properties.AppProperties;
import gg.pingpong.api.global.security.cookie.CookieUtil;
import gg.pingpong.api.global.security.jwt.repository.JwtRedisRepository;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.global.security.jwt.utils.TokenHeaders;
import gg.pingpong.api.global.utils.ApplicationYmlRead;
import gg.repo.user.UserRepository;
import gg.utils.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtRedisRepository jwtRedisRepository;
	private final UserRepository userRepository;
	private final AuthTokenProvider tokenProvider;
	private final AppProperties appProperties;
	private final ApplicationYmlRead applicationYmlRead;
	private final CookieUtil cookieUtil;

	@Transactional
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		String targetUrl;
		try {
			targetUrl = determineTargetUrl(request, response, authentication);
		} catch (Exception e) {
			targetUrl = applicationYmlRead.getFrontUrl();
		}

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}
		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {

		UserPrincipal principal = (UserPrincipal)authentication.getPrincipal();
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(TokenHeaders.REFRESH_TOKEN)) {
				Long existUserId = jwtRedisRepository.getUserIdFromRefToken(cookie.getValue());
				if (existUserId != null && !existUserId.equals(principal.getId())) {
					jwtRedisRepository.deleteRefToken(cookie.getValue());
					return deleteKakaoUser(existUserId, response, authentication);
				}
			}
		}

		// 쿠키 시간 설정
		long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

		// token 설정
		String accessToken = tokenProvider.createToken(principal.getId());
		String refreshToken = tokenProvider.refreshToken(principal.getId());

		cookieUtil.addCookie(response, TokenHeaders.REFRESH_TOKEN, refreshToken,
			(int)(refreshTokenExpiry / 1000));

		jwtRedisRepository.addRefToken(refreshToken, refreshTokenExpiry, principal.getId());
		return UriComponentsBuilder.fromUriString(applicationYmlRead.getFrontUrl())
			.queryParam("token", accessToken)
			.build().toUriString();
	}

	private String deleteKakaoUser(Long existUserId, HttpServletResponse response, Authentication authentication) {
		UserPrincipal principal = (UserPrincipal)authentication.getPrincipal();
		User existUser = userRepository.findById(existUserId).orElseThrow(UserNotFoundException::new);
		User newUser = userRepository.findById(principal.getId()).orElseThrow(UserNotFoundException::new);
		//kakao 계정 사용자가 42 인증
		if (existUser.getRoleType().equals(RoleType.GUEST)) {
			return UriComponentsBuilder.fromUriString(applicationYmlRead.getFrontUrl() + "/users/detail")
				.queryParam("intraId", newUser.getIntraId())
				.queryParam("token", saveAndGetUserAccessToken(response, newUser, existUser))
				.build().toUriString();
		}
		//기존 user 사용자가 카카오 인증
		if (newUser.getRoleType().equals(RoleType.GUEST)) {
			return UriComponentsBuilder.fromUriString(applicationYmlRead.getFrontUrl() + "/users/detail")
				.queryParam("intraId", existUser.getIntraId())
				.queryParam("token", saveAndGetUserAccessToken(response, existUser, newUser))
				.build().toUriString();
		}
		throw new UserNotFoundException();
	}

	private String saveAndGetUserAccessToken(HttpServletResponse response,
		User remainedUser, User deletedUser) {
		remainedUser.updateKakaoId(deletedUser.getKakaoId());
		// 쿠키 시간 설정
		long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
		userRepository.delete(deletedUser);

		// token 설정
		String accessToken = tokenProvider.createToken(remainedUser.getId());
		String refreshToken = tokenProvider.refreshToken(remainedUser.getId());
		jwtRedisRepository.addRefToken(refreshToken, refreshTokenExpiry, remainedUser.getId());

		cookieUtil.addCookie(response, TokenHeaders.REFRESH_TOKEN, refreshToken,
			(int)(refreshTokenExpiry / 1000));
		return accessToken;
	}

	private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
	}

}
