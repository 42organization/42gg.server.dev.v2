package gg.pingpong.api.global.security.service;

import static gg.data.agenda.type.Coalition.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Auth42Token;
import gg.data.agenda.type.Coalition;
import gg.data.pingpong.rank.Rank;
import gg.data.pingpong.rank.Tier;
import gg.data.pingpong.rank.redis.RankRedis;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.SnsType;
import gg.pingpong.api.global.security.UserPrincipal;
import gg.pingpong.api.global.security.info.OAuthUserInfo;
import gg.pingpong.api.global.security.info.OAuthUserInfoFactory;
import gg.pingpong.api.global.security.info.ProviderType;
import gg.pingpong.api.global.utils.aws.AsyncNewUserImageUploader;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.rank.RankRepository;
import gg.repo.rank.TierRepository;
import gg.repo.rank.redis.RankRedisRepository;
import gg.repo.season.SeasonRepository;
import gg.repo.user.Auth42TokenRedisRepository;
import gg.repo.user.UserRepository;
import gg.utils.RedisKeyManager;
import gg.utils.exception.tier.TierNotFoundException;
import gg.utils.external.ApiUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final ApiUtil apiUtil;
	private final UserRepository userRepository;
	private final AsyncNewUserImageUploader asyncNewUserImageUploader;
	private final RankRepository rankRepository;
	private final SeasonRepository seasonRepository;
	private final RankRedisRepository rankRedisRepository;
	private final TierRepository tierRepository;
	private final AgendaProfileRepository agendaProfileRepository;
	private final Auth42TokenRedisRepository auth42TokenRedisRepository;

	@Value("${info.image.defaultUrl}")
	private String defaultImageUrl;
	@Value("${info.web.coalitionUrl}")
	private String coalitionUrl;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User user = super.loadUser(userRequest);

		try {
			return this.process(userRequest, user);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}
	}

	private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) throws IOException {
		ProviderType providerType = ProviderType.keyOf(
			userRequest.getClientRegistration().getRegistrationId().toUpperCase());
		User savedUser;
		OAuthUserInfo userInfo = OAuthUserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
		if (providerType.equals(ProviderType.FORTYTWO)) {
			savedUser = userRepository.findByIntraId(userInfo.getIntraId())
				.orElse(null);
		} else {
			savedUser = userRepository.findByKakaoId(userInfo.getKakaoId())
				.orElse(null);
		}
		if (savedUser == null) {
			savedUser = createUser(userInfo);
			if (providerType.equals(ProviderType.FORTYTWO)) {
				createUserRank(savedUser);
			}
			if (userInfo.getImageUrl().startsWith("https://cdn.intra.42.fr/")) {
				asyncNewUserImageUploader.upload(userInfo.getIntraId(), userInfo.getImageUrl());
			}
		}
		if (agendaProfileRepository.findByUserId(savedUser.getId()).isEmpty()) {
			String token = userRequest.getAccessToken().getTokenValue();
			createProfile(userInfo, savedUser, token);
		}

		Auth42Token auth42Token = new Auth42Token(userInfo.getUserId(), userRequest.getAccessToken().getTokenValue(),
			"s");

		auth42TokenRedisRepository.save42Token(savedUser.getIntraId(), auth42Token);
		return UserPrincipal.create(savedUser, user.getAttributes());
	}

	private void createUserRank(User savedUser) {
		Tier tier = tierRepository.findStartTier().orElseThrow(TierNotFoundException::new);
		seasonRepository.findCurrentAndNewSeason(LocalDateTime.now()).forEach(
			season -> {
				Rank userRank = Rank.from(savedUser, season, season.getStartPpp(), tier);
				rankRepository.save(userRank);
				RankRedis rankRedis = RankRedis.from(savedUser.getId(), savedUser.getIntraId(),
					savedUser.getTextColor(), season.getStartPpp(), tier.getImageUri());
				String hashKey = RedisKeyManager.getHashKey(season.getId());
				rankRedisRepository.addRankData(hashKey, savedUser.getId(), rankRedis);
			}
		);
	}

	private User createUser(OAuthUserInfo userInfo) {
		User user = User.builder()
			.intraId(userInfo.getIntraId())
			.roleType(userInfo.getRoleType())
			.kakaoId(userInfo.getKakaoId())
			.snsNotiOpt(SnsType.EMAIL)
			.racketType(RacketType.NONE)
			.totalExp(0)
			.eMail(userInfo.getEmail())
			.build();
		return userRepository.saveAndFlush(user);
	}

	private void createProfile(OAuthUserInfo userInfo, User user, String accessToken) {
		AgendaProfile agendaProfile = AgendaProfile.builder()
			.userId(user.getId())
			.intraId(userInfo.getIntraId())
			.content("안녕하세요! " + userInfo.getIntraId() + "입니다.")
			.githubUrl(null)
			.coalition(findCoalition(userInfo.getUserId().toString(), accessToken))
			.fortyTwoId(userInfo.getUserId())
			.location(userInfo.getLocation())
			.build();
		agendaProfileRepository.save(agendaProfile);
	}

	private Coalition findCoalition(String id, String accessToken) {
		String url = coalitionUrl.replace("{id}", id);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		// HttpEntity 객체를 생성하여 헤더를 포함한 요청을 보냄
		ParameterizedTypeReference<List<Map<String, Object>>> responseType = new ParameterizedTypeReference<>() {
		};
		List<Map<String, Object>> response = apiUtil.apiCall(url, responseType, headers, HttpMethod.GET);

		if (response != null && !response.isEmpty()) {
			Map<String, Object> coalition = response.get(0);
			String coalitionName = (String)coalition.get("name");
			return Coalition.valueOfCoalition(coalitionName);
		} else {
			return OTHER;
		}
	}
}
