package gg.data.agenda;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auth42Token {
	private String intra42Id;
	private String accessToken;

	public Auth42Token(String intra42Id, String accessToken) {
		this.intra42Id = intra42Id;
		this.accessToken = accessToken;
	}
}
