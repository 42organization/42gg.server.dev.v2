package gg.pingpong.data.user.type;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OauthType {
	KAKAO("kakao"),
	FORTYTWO("fortyTwo"),
	BOTH("both");

	private final String code;

	@JsonCreator
	public static OauthType getEnumFromValue(String value) {
		for (OauthType e : values()) {
			if (e.code.equals(value)) {
				return e;
			} else if (e.code.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
				return e;
			}
		}
		return null;
	}

	public static OauthType of(RoleType roleType, Long kakaoId) {
		if (kakaoId == null) {
			return OauthType.FORTYTWO;
		}
		if (roleType.equals(RoleType.GUEST)) {
			return OauthType.KAKAO;
		}
		return OauthType.BOTH;
	}
}
