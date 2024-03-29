package gg.pingpong.api.global.security.info;

import lombok.Getter;

@Getter
public enum ProviderType {
	FORTYTWO("42"), KAKAO("KAKAO");

	private String key;

	ProviderType(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public static ProviderType keyOf(String key) {
		for (ProviderType value : ProviderType.values()) {
			if (value.key.equalsIgnoreCase(key)) {
				return value;
			}
		}
		return null;
	}
}
