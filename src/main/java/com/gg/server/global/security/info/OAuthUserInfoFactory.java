package com.gg.server.global.security.info;

import com.gg.server.global.security.info.impl.KakaoOAuthUserInfo;
import java.util.Map;

import com.gg.server.global.security.info.impl.FortyTwoOAuthUserInfo;

public class OAuthUserInfoFactory {
    public static OAuthUserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case FORTYTWO: return new FortyTwoOAuthUserInfo(attributes);
            case KAKAO: return new KakaoOAuthUserInfo(attributes);
            //case SLACK: return new FacebookOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
