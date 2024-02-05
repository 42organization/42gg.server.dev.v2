package com.gg.server.global.security.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.data.game.Rank;
import com.gg.server.data.game.Tier;
import com.gg.server.data.game.redis.RankRedis;
import com.gg.server.data.user.User;
import com.gg.server.data.user.type.RacketType;
import com.gg.server.data.user.type.SnsType;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.tier.data.TierRepository;
import com.gg.server.domain.tier.exception.TierNotFoundException;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.security.UserPrincipal;
import com.gg.server.global.security.info.OAuthUserInfo;
import com.gg.server.global.security.info.OAuthUserInfoFactory;
import com.gg.server.global.security.info.ProviderType;
import com.gg.server.global.utils.aws.AsyncNewUserImageUploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;
	private final AsyncNewUserImageUploader asyncNewUserImageUploader;
	private final RankRepository rankRepository;
	private final SeasonRepository seasonRepository;
	private final RankRedisRepository rankRedisRepository;
	private final TierRepository tierRepository;

	@Value("${info.image.defaultUrl}")
	private String defaultImageUrl;

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

	private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
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
		return UserPrincipal.create(savedUser, user.getAttributes());
	}

	private void createUserRank(User savedUser) {
		Tier tier = tierRepository.findStartTier().orElseThrow(TierNotFoundException::new);
		seasonRepository.findCurrentAndNewSeason(LocalDateTime.now()).forEach(
			season -> {
				Rank userRank = Rank.from(savedUser, season, season.getStartPpp(), tier);
				rankRepository.save(userRank);
				RankRedis rankRedis = RankRedis.from(UserDto.from(savedUser), season.getStartPpp(), tier.getImageUri());
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
}
