package gg.pingpong.data.user.type;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {

	ADMIN("ROLE_ADMIN", "관리자"),
	USER("ROLE_USER", "일반 사용자"),
	GUEST("ROLE_GUEST", "게스트");

	private final String key;
	private final String displayName;

	public static RoleType of(String key) {
		return Arrays.stream(RoleType.values())
			.filter(r -> r.getKey().equals(key))
			.findAny()
			.orElse(USER);
	}
}

