package gg.pingpong.utils.exception.user;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.AuthenticationException;

public class TokenNotValidException extends AuthenticationException {
	public TokenNotValidException() {
		super("Authentication error", ErrorCode.UNAUTHORIZED);
	}
}
