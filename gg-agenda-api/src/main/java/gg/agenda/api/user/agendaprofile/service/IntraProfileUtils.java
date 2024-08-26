package gg.agenda.api.user.agendaprofile.service;

import static gg.utils.exception.ErrorCode.*;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraAchievement;
import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraImage;
import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraProfile;
import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraProfileResponse;
import gg.auth.FortyTwoAuthUtil;
import gg.utils.exception.custom.AuthenticationException;
import gg.utils.external.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class IntraProfileUtils {
	private static final String INTRA_PROFILE_URL = "https://api.intra.42.fr/v2/me";

	private final FortyTwoAuthUtil fortyTwoAuthUtil;

	private final ApiUtil apiUtil;

	public IntraProfile getIntraProfile() {
		IntraProfileResponse intraProfileResponse = requestIntraProfile();
		intraProfileResponseValidation(intraProfileResponse);
		IntraImage intraImage = intraProfileResponse.getImage();
		List<IntraAchievement> intraAchievements = intraProfileResponse.getAchievements();
		return new IntraProfile(intraImage.getLink(), intraAchievements);
	}


	public IntraProfile getIntraProfile(String intraId) {
		return null;
	}

	private IntraProfileResponse requestIntraProfile() {
		try {
			String accessToken = fortyTwoAuthUtil.getAccessToken();
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);
			return apiUtil.apiCall(INTRA_PROFILE_URL, IntraProfileResponse.class, headers, HttpMethod.GET);
		} catch (Exception e) {
			String accessToken = fortyTwoAuthUtil.refreshAccessToken();
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);
			return apiUtil.apiCall(INTRA_PROFILE_URL, IntraProfileResponse.class, headers, HttpMethod.GET);
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