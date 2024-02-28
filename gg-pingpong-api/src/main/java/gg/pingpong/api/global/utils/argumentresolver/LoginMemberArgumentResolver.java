package gg.pingpong.api.global.utils.argumentresolver;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.global.utils.HeaderUtil;
import gg.pingpong.api.user.user.dto.UserDto;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.user.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
	private final UserRepository userRepository;
	private final AuthTokenProvider tokenProvider;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
		boolean hasUserType = UserDto.class.isAssignableFrom(parameter.getParameterType());
		return hasLoginAnnotation && hasUserType;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		String accessToken = HeaderUtil.getAccessToken(request);
		Long loginUserId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User user = userRepository.findById(loginUserId).orElseThrow();
		UserDto userDto = UserDto.from(user);
		return userDto;
	}
}
