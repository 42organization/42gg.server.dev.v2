package com.gg.server.global.security.handler;

import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.global.security.CookieUtil;
import com.gg.server.global.security.config.properties.AppProperties;
import com.gg.server.global.security.domain.UserPrincipal;
import com.gg.server.global.security.info.OAuthUserInfo;
import com.gg.server.global.security.info.OAuthUserInfoFactory;
import com.gg.server.global.security.jwt.Token;
import com.gg.server.global.security.repository.OAuthAuthorizationRequestBasedOnCookieRepository;
import com.gg.server.global.security.repository.UserRefreshTokenRepository;
import com.gg.server.global.security.service.JwtTokenService;
import com.gg.server.global.security.token.AuthToken;
import com.gg.server.global.security.token.AuthTokenProvider;
import com.gg.server.global.types.security.ProviderType;
import com.gg.server.global.types.user.RoleType;
import com.gg.server.global.utils.ApplicationYmlRead;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;

@Component
@RequiredArgsConstructor
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    private final String REFRESH_TOKEN = "refresh_token";
    private final String ACCESS_TOKEN = "access_token";
    private final AuthTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final AppProperties appProperties;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final JwtTokenService jwtTokenService;
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

        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                user.getId(),
                user.getIntraId(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        // refresh 토큰 설정
        AuthToken refreshToken = tokenProvider.createAuthToken(
                user.getId(),
                user.getIntraId(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // DB 저장
        Token userToken = jwtTokenService.generateToken(user.getId(), accessToken, refreshToken);
        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.addCookie(response, REFRESH_TOKEN, userToken.getRefreshToken(), cookieMaxAge);
        CookieUtil.addCookie(response, ACCESS_TOKEN, userToken.getAccessToken(), cookieMaxAge);

        return UriComponentsBuilder.fromUriString(applicationYmlRead.getFrontUrl())
                .queryParam("token", userToken.getAccessToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        //authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
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

//    private boolean isAuthorizedRedirectUri(String uri) {
//        URI clientRedirectUri = URI.create(uri);
//
//        return appProperties.getOauth2().getAuthorizedRedirectUris()
//                .stream()
//                .anyMatch(authorizedRedirectUri -> {
//                    // Only validate host and port. Let the clients use different paths if they want to
//                    URI authorizedURI = URI.create(authorizedRedirectUri);
//                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
//                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
//                        return true;
//                    }
//                    return false;
//                });
//    }
}


