package com.gg.server.global.security.handler;

import com.gg.server.global.security.cookie.CookieUtil;
import com.gg.server.global.security.config.properties.AppProperties;
import com.gg.server.global.security.service.UserPrincipal;
import com.gg.server.global.security.info.OAuthUserInfo;
import com.gg.server.global.security.info.OAuthUserInfoFactory;
import com.gg.server.global.security.jwt.AuthTokenProvider;
import com.gg.server.global.security.info.ProviderType;
import com.gg.server.global.utils.ApplicationYmlRead;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    private final String REFRESH_TOKEN = "refresh_token";
    private final String ACCESS_TOKEN = "access_token";
    private final AuthTokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final ApplicationYmlRead applicationYmlRead;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
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
//        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
//                .map(Cookie::getValue);
//        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
//            throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
//        }
//        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        ProviderType providerType = ProviderType.keyOf (authToken.getAuthorizedClientRegistrationId().toUpperCase());

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        OAuthUserInfo userInfo = OAuthUserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());

        // token 설정
        String accessToken = tokenProvider.createToken(authentication);
        String refreshToken = tokenProvider.refreshToken(authentication);
        // 쿠키 시간 설정
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        long accessTokenExpiry = appProperties.getAuth().getTokenExpiry();

        // DB 저장

        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken, (int)(refreshTokenExpiry / 1000));
        CookieUtil.addCookie(response, ACCESS_TOKEN, accessToken, (int) (accessTokenExpiry / 1000));

        return UriComponentsBuilder.fromUriString(applicationYmlRead.getFrontUrl())
                .queryParam("token", accessToken)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
        if (authorities == null) {
            return false;
        }

        for (GrantedAuthority grantedAuthority : authorities) {
            if (authority.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}


