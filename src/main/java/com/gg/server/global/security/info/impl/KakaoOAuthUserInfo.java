package com.gg.server.global.security.info.impl;

import com.gg.server.domain.user.type.RoleType;
import com.gg.server.global.security.info.OAuthUserInfo;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

public class KakaoOAuthUserInfo extends OAuthUserInfo {
    @Value("${info.image.defaultUrl}")
    private String defaultImageUrl;

    public KakaoOAuthUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getIntraId() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickName = "[GUEST]" + properties.get("nickname").toString();
        return nickName;
//        return properties.get("nickname").toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return kakaoAccount.get("email").toString();
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return properties.get("profile_image").toString();
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.GUEST;
    }
}
