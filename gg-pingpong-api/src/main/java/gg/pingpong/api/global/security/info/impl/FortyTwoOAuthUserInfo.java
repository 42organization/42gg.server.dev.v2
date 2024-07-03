package gg.pingpong.api.global.security.info.impl;

import gg.data.agenda.type.Coalition;
import gg.data.agenda.type.Location;
import gg.data.user.type.RoleType;
import gg.pingpong.api.global.security.info.OAuthUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static gg.data.agenda.type.Coalition.OTHER;
import static gg.data.agenda.type.Location.MIX;

public class FortyTwoOAuthUserInfo extends OAuthUserInfo {

	@Value("${info.image.defaultUrl}")
	private String defaultImageUrl;
	private final String coalitionUrl = "https://api.intra.42.fr/v2/users/{id}/coalitions";
	private RestTemplate restTemplate;

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
		Map<String, Object> image = (Map<String, Object>) attributes.get("image");
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
	public Coalition getCoalition() {
		String id = attributes.get("id").toString();
		String url = coalitionUrl.replace("{id}", id);
		List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

		if (response != null && !response.isEmpty()) {
			Map<String, Object> coalition = response.get(0);
			String coalitionName = (String) coalition.get("name");
			return Coalition.valueOf(coalitionName.toUpperCase());
		} else {
			return OTHER;
		}
	}

	@Override
	public Location getLocation() {
		List<Map<String, Object>> campuses = (List<Map<String, Object>>) attributes.get("campus");
		if (campuses != null && !campuses.isEmpty()) {
			Map<String, Object> campus = campuses.get(0);
			String campusName = (String) campus.get("city");
			return Location.valueOf(campusName.toUpperCase());
		} else {
			return MIX;
		}
	}
}
