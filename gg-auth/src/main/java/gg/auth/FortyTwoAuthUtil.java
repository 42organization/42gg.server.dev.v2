package gg.auth;

import static gg.utils.exception.ErrorCode.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;
import gg.utils.exception.user.TokenNotValidException;
import gg.utils.external.ApiUtil;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FortyTwoAuthUtil {
	private final ApiUtil apiUtil;
	private final OAuth2AuthorizedClientService authorizedClientService;

	public String getAccessToken() {
		Authentication authentication = getAuthenticationFromContext();
		OAuth2AuthorizedClient client = getClientFromAuthentication(authentication);
		if (Objects.isNull(client)) {
			throw new TokenNotValidException();
		}
		return client.getAccessToken().getTokenValue();
	}

	/**
	 * 토큰 갱신
	 * @return 갱신된 OAuth2AuthorizedClient
	 */
	public String refreshAccessToken() {
		Authentication authentication = getAuthenticationFromContext();
		OAuth2AuthorizedClient client = getClientFromAuthentication(authentication);
		ClientRegistration registration = client.getClientRegistration();

		OAuth2AuthorizedClient newClient = requestNewClient(client, registration);

		authorizedClientService.removeAuthorizedClient(
			registration.getRegistrationId(), client.getPrincipalName());
		authorizedClientService.saveAuthorizedClient(newClient, authentication);

		return newClient.getAccessToken().getTokenValue();
	}

	private Authentication getAuthenticationFromContext() {
		SecurityContext context = SecurityContextHolder.getContext();
		return context.getAuthentication();
	}

	private OAuth2AuthorizedClient getClientFromAuthentication(Authentication authentication) {
		OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken)authentication;
		String registrationId = oauthToken.getAuthorizedClientRegistrationId();
		return authorizedClientService.loadAuthorizedClient(registrationId, oauthToken.getName());
	}

	private OAuth2AuthorizedClient requestNewClient(OAuth2AuthorizedClient client, ClientRegistration registration) {
		if (Objects.isNull(client.getRefreshToken())) {
			throw new NotExistException(AUTH_NOT_FOUND);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "refresh_token");
		params.add("refresh_token", client.getRefreshToken().getTokenValue());
		params.add("client_id", registration.getClientId());
		params.add("client_secret", registration.getClientSecret());

		List<Map<String, Object>> responseBody = apiUtil.apiCall(
			registration.getProviderDetails().getTokenUri(),
			List.class,
			headers,
			params,
			HttpMethod.POST
		);
		if (Objects.isNull(responseBody) || responseBody.isEmpty()) {
			throw new NotExistException(ErrorCode.AUTH_NOT_FOUND);
		}
		return createNewClientFromApiResponse(responseBody.get(0), client);
	}

	private OAuth2AuthorizedClient createNewClientFromApiResponse(
		Map<String, Object> response, OAuth2AuthorizedClient client) {

		OAuth2AccessToken newAccessToken = new OAuth2AccessToken(
			OAuth2AccessToken.TokenType.BEARER,
			(String)response.get("access_token"),
			Instant.now(),
			Instant.now().plusSeconds((Integer)response.get("expires_in"))
		);

		OAuth2RefreshToken newRefreshToken = new OAuth2RefreshToken(
			(String)response.get("refresh_token"),
			Instant.now()
		);

		return new OAuth2AuthorizedClient(
			client.getClientRegistration(),
			client.getPrincipalName(),
			newAccessToken,
			newRefreshToken
		);
	}
}
