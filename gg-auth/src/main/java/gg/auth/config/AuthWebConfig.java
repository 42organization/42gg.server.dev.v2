package gg.auth.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import gg.auth.argumentresolver.LoginMemberArgumentResolver;
import gg.auth.utils.AuthTokenProvider;
import gg.repo.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AuthWebConfig implements WebMvcConfigurer {
	private final UserRepository userRepository;
	private final AuthTokenProvider tokenProvider;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new LoginMemberArgumentResolver(userRepository, tokenProvider));
	}
}
