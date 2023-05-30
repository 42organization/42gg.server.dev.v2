package com.gg.server.global.security.handler;

import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.global.security.UserPrincipal;
import com.gg.server.global.security.cookie.CookieUtil;
import com.gg.server.global.security.config.properties.AppProperties;
import com.gg.server.global.security.jwt.repository.JwtRedisRepository;
import com.gg.server.global.security.jwt.utils.TokenHeaders;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.global.utils.ApplicationYmlRead;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtRedisRepository jwtRedisRepository;

    private final AuthTokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final ApplicationYmlRead applicationYmlRead;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // 쿠키 시간 설정
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        // token 설정
        String accessToken = tokenProvider.createToken(principal.getId());
        String refreshToken = tokenProvider.refreshToken(principal.getId());

        CookieUtil.addCookie(response, TokenHeaders.REFRESH_TOKEN, refreshToken,
                        (int)(refreshTokenExpiry / 1000), applicationYmlRead.getDomain());

        String refTokenKey = RedisKeyManager.getRefKey(principal.getId());
        if (jwtRedisRepository.getRefToken(refTokenKey) != null)
            jwtRedisRepository.deleteRefToken(refTokenKey);
        jwtRedisRepository.addRefToken(refTokenKey, refreshToken, refreshTokenExpiry);
        return UriComponentsBuilder.fromUriString(applicationYmlRead.getFrontUrl())
                .queryParam("token", accessToken)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
    }


}


