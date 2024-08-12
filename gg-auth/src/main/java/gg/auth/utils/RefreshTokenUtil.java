package gg.auth.utils;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import gg.data.agenda.Auth42Token;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;
import gg.utils.external.ApiUtil;
import lombok.RequiredArgsConstructor;

@Deprecated
@Component
@RequiredArgsConstructor
public class RefreshTokenUtil {
	private final ApiUtil apiUtil;

	@Value("${spring.security.oauth2.client.registration.42.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.42.client-secret}")
	private String clientSecret;

	@Value("${spring.security.oauth2.client.registration.42.redirect-uri}")
	private String url;

	public Auth42Token refreshAuth42Token(Auth42Token auth42Token) {
		final HttpHeaders headers = new HttpHeaders();

		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		MultiValueMap<String, String> params = createTokenRefreshParam(auth42Token.getRefreshToken());

		List<Map<String, Object>> response = apiUtil.apiCall(url, List.class, headers, params, HttpMethod.POST);
		if (response == null || response.isEmpty()) {
			throw new NotExistException(ErrorCode.AUTH_NOT_FOUND);
		}
		Map<String, Object> map = response.get(0);
		return new Auth42Token(auth42Token.getIntra42Id(), (String)map.get("access_token"),
			(String)map.get("refresh_token"));
	}

	private MultiValueMap<String, String> createTokenRefreshParam(final String refreshToken) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "refresh_token");
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("refresh_token", refreshToken);
		return params;
	}
}
