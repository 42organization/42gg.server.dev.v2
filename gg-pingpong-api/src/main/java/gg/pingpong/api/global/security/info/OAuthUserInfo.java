package gg.pingpong.api.global.security.info;

import java.util.Map;

import gg.data.agenda.type.Location;
import gg.data.user.type.RoleType;

public abstract class OAuthUserInfo {
	protected Map<String, Object> attributes;

	public OAuthUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public abstract String getIntraId();

	public abstract String getEmail();

	public abstract String getImageUrl();

	public abstract RoleType getRoleType();

	public abstract Long getKakaoId();

	public abstract String getUserId();

	public abstract Location getLocation();
}
