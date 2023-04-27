package com.gg.server.global.security.info;

import java.util.Map;

import com.gg.server.global.security.info.impl.FortyTwoOAuthUserInfo;

public class OAuthUserInfoFactory {
    public static OAuthUserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case FORTYTWO: return new FortyTwoOAuthUserInfo(attributes);
            //case SLACK: return new FacebookOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}