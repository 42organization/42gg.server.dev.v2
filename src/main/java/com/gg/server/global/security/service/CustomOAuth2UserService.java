package com.gg.server.global.security.service;

import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.global.security.domain.UserPrincipal;
import com.gg.server.global.security.info.OAuthUserInfo;
import com.gg.server.global.security.info.OAuthUserInfoFactory;
import com.gg.server.global.types.security.ProviderType;
import com.gg.server.global.types.user.RacketType;
import com.gg.server.global.types.user.RoleType;
import com.gg.server.global.types.user.SnsType;
import com.gg.server.global.utils.AsyncNewUserImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final AsyncNewUserImageUploader asyncNewUserImageUploader;

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
        ProviderType providerType = ProviderType.keyOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuthUserInfo userInfo = OAuthUserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        User savedUser = userRepository.findByIntraId(userInfo.getIntraId())
                .orElse(null);
        if (savedUser != null)
        {
            updateUser(savedUser , userInfo);
        } else {
            savedUser = createUser(userInfo);
            if (userInfo.getImageUrl() == null) {
                savedUser.setImageUri(defaultImageUrl);
            }
            else if (userInfo.getImageUrl().startsWith("https://cdn.intra.42.fr/")) {
                asyncNewUserImageUploader.upload(userInfo.getIntraId(), userInfo.getImageUrl());
            }
        }
        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    private User createUser(OAuthUserInfo userInfo) {
        User user = User.builder()
                .intraId(userInfo.getIntraId())
                .roleType(RoleType.USER)
                .imageUri(userInfo.getImageUrl())
                .snsNotiOpt(SnsType.EMAIL)
                .statusMessage("")
                .racketType(RacketType.NONE)
                .totalExp(0)
                .eMail(userInfo.getEmail())
                .build();
        return userRepository.saveAndFlush(user);
    }

    private User updateUser(User user, OAuthUserInfo userInfo) {
        if (userInfo.getIntraId() != null && !user.getIntraId().equals(userInfo.getIntraId())) {
            user.setIntraId(userInfo.getIntraId());
        }
        return user;
    }
}
