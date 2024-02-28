package gg.utils.exception.user;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.AuthenticationException;

public class TokenNotValidException extends AuthenticationException {
	public TokenNotValidException() {
		super("Authentication error", ErrorCode.UNAUTHORIZED);
	}
}
