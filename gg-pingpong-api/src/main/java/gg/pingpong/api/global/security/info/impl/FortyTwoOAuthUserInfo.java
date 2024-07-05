package gg.pingpong.api.global.security.info.impl;

import static gg.data.agenda.type.Location.*;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import gg.data.agenda.type.Location;
import gg.data.user.type.RoleType;
import gg.pingpong.api.global.security.info.OAuthUserInfo;

public class FortyTwoOAuthUserInfo extends OAuthUserInfo {

	@Value("${info.image.defaultUrl}")
	private String defaultImageUrl;

	public FortyTwoOAuthUserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getIntraId() {
		return attributes.get("login").toString();
	}

	public String getEmail() {
		return attributes.get("email").toString();
	}

	public String getImageUrl() {
		Map<String, Object> image = (Map<String, Object>)attributes.get("image");
		if (image == null) {
			return defaultImageUrl;
		}
		if (image.get("link") == null) {
			return defaultImageUrl;
		}
		return image.get("link").toString();
	}

	@Override
	public RoleType getRoleType() {
		return RoleType.USER;
	}

	@Override
	public Long getKakaoId() {
		return null;
	}

	@Override
	public String getUserId() {
		return attributes.get("id").toString();
	}

	@Override
	public Location getLocation() {
		List<Map<String, Object>> campuses = (List<Map<String, Object>>)attributes.get("campus");
		if (campuses != null && !campuses.isEmpty()) {
			Map<String, Object> campus = campuses.get(0);
			String campusName = (String)campus.get("city");
			return Location.valueOfLocation(campusName);
		} else {
			return MIX;
		}
	}
}
