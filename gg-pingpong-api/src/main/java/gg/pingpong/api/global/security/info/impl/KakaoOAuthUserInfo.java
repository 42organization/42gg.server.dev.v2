package gg.pingpong.api.global.security.info.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import gg.pingpong.api.global.security.info.OAuthUserInfo;
import gg.pingpong.data.user.type.RoleType;

public class KakaoOAuthUserInfo extends OAuthUserInfo {
	@Value("${info.image.defaultUrl}")
	private String defaultImageUrl;

	public KakaoOAuthUserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getIntraId() {
		return "GUEST" + attributes.get("id").toString();
	}

	@Override
	public String getEmail() {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		if (kakaoAccount.get("email") == null) {
			return null;
		}
		return kakaoAccount.get("email").toString();
	}

	@Override
	public String getImageUrl() {
		Map<String, Object> properties = (Map<String, Object>)attributes.get("properties");
		return properties.get("profile_image").toString();
	}

	@Override
	public RoleType getRoleType() {
		return RoleType.GUEST;
	}

	@Override
	public Long getKakaoId() {
		return (Long)attributes.get("id");
	}
}
