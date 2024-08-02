package gg.data.agenda;

public class Auth42Token {
	private final String intra42Id;
	private final String accessToken;

	public Auth42Token(String intra42Id, String accessToken) {
		this.intra42Id = intra42Id;
		this.accessToken = accessToken;
	}
}
