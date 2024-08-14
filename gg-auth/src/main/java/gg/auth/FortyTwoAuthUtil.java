package gg.auth;

import static gg.utils.exception.ErrorCode.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;
import gg.utils.external.ApiUtil;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FortyTwoAuthUtil {
	private final ApiUtil apiUtil;
	private final OAuth2AuthorizedClientService authorizedClientService;

	/**
	 * OAuth2AuthorizedClient 조회
	 * @param oauthToken OAuth2AuthenticationToken
	 * @return OAuth2AuthorizedClient
	 */
	public OAuth2AuthorizedClient getOAuth2AuthorizedClient(OAuth2AuthenticationToken oauthToken) {
		String registrationId = oauthToken.getAuthorizedClientRegistrationId();
		OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(registrationId,
			oauthToken.getName());
		if (client.getRefreshToken() == null) {
			throw new NotExistException(AUTH_NOT_FOUND);
		}
		return client;
	}

	/**
	 * 토큰 갱신
	 * @param client OAuth2AuthorizedClient
	 * @param authentication Authentication
	 * @return 갱신된 OAuth2AuthorizedClient
	 */
	public OAuth2AuthorizedClient refreshAccessToken(OAuth2AuthorizedClient client, Authentication authentication) {
		try {
			ClientRegistration registration = client.getClientRegistration();
			if (client.getRefreshToken() == null) {
				throw new NotExistException(AUTH_NOT_FOUND);
			}

			String tokenUri = registration.getProviderDetails().getTokenUri();
			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("grant_type", "refresh_token");
			params.add("refresh_token", client.getRefreshToken().getTokenValue());
			params.add("client_id", registration.getClientId());
			params.add("client_secret", registration.getClientSecret());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			List<Map<String, Object>> responseBody = apiUtil.apiCall(tokenUri, List.class, headers, params,
				HttpMethod.POST);
			if (responseBody == null || responseBody.isEmpty()) {
				throw new NotExistException(ErrorCode.AUTH_NOT_FOUND);
			}
			Map<String, Object> map = responseBody.get(0);

			OAuth2AccessToken newAccessToken = new OAuth2AccessToken(
				OAuth2AccessToken.TokenType.BEARER,
				(String)map.get("access_token"),
				Instant.now(),
				Instant.now().plusSeconds((Integer)map.get("expires_in"))
			);

			OAuth2RefreshToken newRefreshToken = new OAuth2RefreshToken(
				(String)map.get("refresh_token"),
				Instant.now()
			);

			OAuth2AuthorizedClient newClient = new OAuth2AuthorizedClient(
				registration, client.getPrincipalName(), newAccessToken, newRefreshToken);

			String principalName = authentication.getName();
			authorizedClientService.removeAuthorizedClient(registration.getRegistrationId(), principalName);
			authorizedClientService.saveAuthorizedClient(newClient, authentication);

			return newClient;
		} catch (RestClientException e) {
			throw new NotExistException(AUTH_NOT_FOUND);
		}
	}
}
