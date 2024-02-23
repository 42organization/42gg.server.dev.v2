package gg.pingpong.api.global.security.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import gg.pingpong.api.global.security.config.properties.CorsProperties;
import gg.pingpong.api.global.security.handler.OAuthAuthenticationSuccessHandler;
import gg.pingpong.api.global.security.jwt.utils.TokenAuthenticationFilter;
import gg.pingpong.api.global.security.repository.OAuthAuthorizationRequestBasedOnCookieRepository;
import gg.pingpong.api.global.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CustomUserDetailsService userDetailsService;
	private final OAuthAuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final CorsProperties corsProperties;
	private final TokenAuthenticationFilter tokenAuthenticationFilter;
	private final OAuthAuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.cors()
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
			.antMatchers("/pingpong/admin/**").hasRole("ADMIN")
			.antMatchers(HttpMethod.PUT, "/pingpong/users/{intraId}").hasAnyRole("USER", "ADMIN")
			.antMatchers(HttpMethod.POST, "/pingpong/match").hasAnyRole("USER", "ADMIN")
			.antMatchers(HttpMethod.POST, "/pingpong/tournaments/{tournamentId}/users").hasAnyRole("USER", "ADMIN")
			.antMatchers("/login", "/oauth2/authorization/**", "/", "/pingpong/users/oauth/**",
				"/pingpong/users/accesstoken", "/actuator/**",
				"/swagger-ui/**", "/swagger-ui**", "/v3/api-docs/**", "/v3/api-docs**", "/api-docs").permitAll()
			.anyRequest().authenticated()
			.and()
			.csrf().disable()
			.formLogin().disable()
			.httpBasic().disable()
			.exceptionHandling()
			.authenticationEntryPoint(new RestAuthenticationEntryPoint())
			.and()
			.oauth2Login()
			.authorizationEndpoint()
			.baseUri("/oauth2/authorization")
			.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository)
			.and()
			.successHandler(oAuth2AuthenticationSuccessHandler);

		http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
		corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
		corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
		corsConfig.setAllowCredentials(true);
		corsConfig.setMaxAge(corsConfig.getMaxAge());
		corsConfigSource.registerCorsConfiguration("/**", corsConfig);
		return corsConfigSource;
	}

}
