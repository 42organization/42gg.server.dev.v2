package gg.agenda.api.user.agendaprofile.service;

import static gg.utils.exception.ErrorCode.*;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraAchievement;
import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraImage;
import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraProfile;
import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraProfileResponse;
import gg.auth.FortyTwoAuthUtil;
import gg.utils.cookie.CookieUtil;
import gg.utils.exception.custom.AuthenticationException;
import gg.utils.external.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class IntraProfileUtils {

	private static final String INTRA_PROFILE_URL = "https://api.intra.42.fr/v2/me";
	private static final String INTRA_USERS_URL = "https://api.intra.42.fr/v2/users/";

	private final FortyTwoAuthUtil fortyTwoAuthUtil;

	private final ApiUtil apiUtil;

	private final CookieUtil cookieUtil;

	public IntraProfile getIntraProfile(HttpServletResponse response) {
		try {
			IntraProfileResponse intraProfileResponse = requestIntraProfile(INTRA_PROFILE_URL);
			intraProfileResponseValidation(intraProfileResponse);
			IntraImage intraImage = intraProfileResponse.getImage();
			List<IntraAchievement> intraAchievements = intraProfileResponse.getAchievements();
			return new IntraProfile(intraImage.getLink(), intraAchievements);
		} catch (Exception e) {
			log.error("42 Intra Profile API 호출 실패", e);
			cookieUtil.deleteCookie(response, "refresh_token");
			throw new AuthenticationException(AUTH_NOT_FOUND);
		}
	}

	public IntraProfile getIntraProfile(String intraId, HttpServletResponse response) {
		try {
			IntraProfileResponse intraProfileResponse = requestIntraProfile(INTRA_USERS_URL + intraId);
			intraProfileResponseValidation(intraProfileResponse);
			IntraImage intraImage = intraProfileResponse.getImage();
			List<IntraAchievement> intraAchievements = intraProfileResponse.getAchievements();
			return new IntraProfile(intraImage.getLink(), intraAchievements);
		} catch (Exception e) {
			log.error("42 Intra Profile API 호출 실패", e);
			cookieUtil.deleteCookie(response, "refresh_token");
			throw new AuthenticationException(AUTH_NOT_FOUND);
		}
	}

	private IntraProfileResponse requestIntraProfile(String requestUrl) {
		try {
			String accessToken = fortyTwoAuthUtil.getAccessToken();
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);
			return apiUtil.apiCall(requestUrl, IntraProfileResponse.class, headers, HttpMethod.GET);
		} catch (Exception e) {
			String accessToken = fortyTwoAuthUtil.refreshAccessToken();
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);
			return apiUtil.apiCall(requestUrl, IntraProfileResponse.class, headers, HttpMethod.GET);
		}
	}

	private void intraProfileResponseValidation(IntraProfileResponse intraProfileResponse) {
		if (Objects.isNull(intraProfileResponse)) {
			throw new AuthenticationException(AUTH_NOT_FOUND);
		}
		if (Objects.isNull(intraProfileResponse.getImage())) {
			throw new AuthenticationException(AUTH_NOT_FOUND);
		}
		if (Objects.isNull(intraProfileResponse.getAchievements())) {
			throw new AuthenticationException(AUTH_NOT_FOUND);
		}
		if (Objects.isNull(intraProfileResponse.getImage().getLink())) {
			throw new AuthenticationException(AUTH_NOT_FOUND);
		}
	}
}
