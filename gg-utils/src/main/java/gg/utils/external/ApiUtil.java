package gg.utils.external;

import static gg.utils.exception.ErrorCode.*;

import java.util.List;
import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.DefaultRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gg.utils.exception.custom.NotExistException;
import gg.utils.exception.user.TokenNotValidException;

@Component
public class ApiUtil {
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final OAuth2AuthorizedClientService authorizedClientService;

	public ApiUtil(ObjectMapper objectMapper, RestTemplateBuilder restTemplateBuilder,
		OAuth2AuthorizedClientService authorizedClientService) {
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplateBuilder.build();
		this.authorizedClientService = authorizedClientService;
	}

	public <T> T apiCall(String url, Class<T> responseType, HttpHeaders headers,
		MultiValueMap<String, String> parameters, HttpMethod method) {
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);
		ResponseEntity<T> res = restTemplate.exchange(url, method, request, responseType);
		if (!res.getStatusCode().is2xxSuccessful()) {
			throw new RuntimeException("api call error");
		}
		return res.getBody();
	}

	public <T> T apiCall(String url, Class<T> responseType, HttpHeaders headers,
		Map<String, String> bodyJson, HttpMethod method) {
		String contentBody = null;
		try {
			contentBody = objectMapper.writeValueAsString(bodyJson);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		HttpEntity<String> request = new HttpEntity<>(contentBody, headers);
		ResponseEntity<T> res = restTemplate.exchange(url, method, request, responseType);
		if (!res.getStatusCode().is2xxSuccessful()) {
			throw new RuntimeException("api call error");
		}
		return res.getBody();
	}

	public <T> T apiCall(String url, Class<T> responseType, HttpHeaders headers, HttpMethod method) {
		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<T> res = restTemplate.exchange(url, method, request, responseType);
		if (!res.getStatusCode().is2xxSuccessful()) {
			throw new TokenNotValidException();
		}
		return res.getBody();
	}

	public <T> T apiCall(String url, ParameterizedTypeReference<T> responseType, HttpHeaders headers,
		HttpMethod method) {
		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<T> res = restTemplate.exchange(url, method, request, responseType);
		if (!res.getStatusCode().is2xxSuccessful()) {
			throw new TokenNotValidException();
		}
		return res.getBody();
	}

	/**
	 * API 호출
	 * @param url 호출할 URL
	 * @param accessToken 액세스 토큰
	 * @param responseType 응답 타입
	 * @return 응답
	 */
	public List<Map<String, Object>> callApiWithAccessToken(String url, String accessToken,
		ParameterizedTypeReference<List<Map<String, Object>>> responseType) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		return apiCall(url, responseType, headers, HttpMethod.GET);
	}

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
			OAuth2RefreshTokenGrantRequest grantRequest = new OAuth2RefreshTokenGrantRequest(
				registration, client.getAccessToken(), client.getRefreshToken());

			OAuth2AccessTokenResponse tokenResponse = new DefaultRefreshTokenTokenResponseClient()
				.getTokenResponse(grantRequest);

			OAuth2AccessToken newAccessToken = tokenResponse.getAccessToken();
			OAuth2RefreshToken newRefreshToken = tokenResponse.getRefreshToken();

			OAuth2AuthorizedClient newClient = new OAuth2AuthorizedClient(
				registration, client.getPrincipalName(), newAccessToken, newRefreshToken);
			String principalName = SecurityContextHolder.getContext().getAuthentication().getName();

			authorizedClientService.removeAuthorizedClient(registration.getRegistrationId(), principalName);
			authorizedClientService.saveAuthorizedClient(newClient, authentication);
			return newClient;
		} catch (OAuth2AuthorizationException e) { // Refresh 토큰도 만료된 경우
			throw new NotExistException(AUTH_NOT_FOUND);
		}
	}
}
