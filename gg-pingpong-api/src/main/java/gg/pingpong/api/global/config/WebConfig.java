package gg.pingpong.api.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.global.utils.argumentresolver.LoginMemberArgumentResolver;
import gg.pingpong.api.global.utils.querytracker.LoggingInterceptor;
import gg.pingpong.repo.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final UserRepository userRepository;
	private final AuthTokenProvider tokenProvider;
	private final LoggingInterceptor loggingInterceptor;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new LoginMemberArgumentResolver(userRepository, tokenProvider));
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loggingInterceptor);
	}
}
