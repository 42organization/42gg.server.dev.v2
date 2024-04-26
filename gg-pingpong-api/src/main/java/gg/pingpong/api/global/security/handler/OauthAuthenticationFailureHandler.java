package gg.pingpong.api.global.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import gg.pingpong.api.global.utils.ApplicationYmlRead;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OauthAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final ApplicationYmlRead applicationYmlRead;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {
		exception.printStackTrace();
		response.sendRedirect(applicationYmlRead.getFrontUrl() + "/404");
	}
}
