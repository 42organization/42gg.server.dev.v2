package gg.pingpong.api.global.security.info.impl;

import gg.data.agenda.type.Coalition;
import gg.data.agenda.type.Location;
import gg.data.user.type.RoleType;
import gg.pingpong.api.global.security.info.OAuthUserInfo;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

import static gg.data.agenda.type.Coalition.OTHER;
import static gg.data.agenda.type.Location.MIX;

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
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		if (kakaoAccount.get("email") == null) {
			return null;
		}
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

	@Override
	public Long getKakaoId() {
		return (Long) attributes.get("id");
	}

	@Override
	public Coalition getCoalition() {
		return OTHER;
	}

	@Override
	public Location getLocation() {
		return MIX;
	}
}
