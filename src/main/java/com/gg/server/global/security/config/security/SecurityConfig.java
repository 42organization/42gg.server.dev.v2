package com.gg.server.global.security.config.security;

import com.gg.server.global.security.TokenAuthenticationFilter;
import com.gg.server.global.security.config.properties.CorsProperties;
import com.gg.server.global.security.exception.RestAuthenticationEntryPoint;
import com.gg.server.global.security.handler.OAuthAuthenticationFailureHandler;
import com.gg.server.global.security.handler.OAuthAuthenticationSuccessHandler;
import com.gg.server.global.security.handler.TokenAccessDeniedHandler;
import com.gg.server.global.security.repository.OAuthAuthorizationRequestBasedOnCookieRepository;
import com.gg.server.global.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final CustomOAuth2UserService oAuth2UserService;
    private final  OAuthAuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final OAuthAuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CorsProperties corsProperties;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestAuthorizationRequestRepository;
    @Override
    protected void configure(HttpSecurity http) throws Exception {

            http
                    .cors()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                        .antMatchers("/login", "/oauth2/authorization/**", "/").permitAll()
                        .antMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                    .and()
                    .csrf().disable()
                    .formLogin().disable()
                    .httpBasic().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                    .accessDeniedHandler(tokenAccessDeniedHandler)
                    .and()
                    .oauth2Login()
                    .authorizationEndpoint()
                    .baseUri("/oauth2/authorization")
                    .authorizationRequestRepository(authorizationRequestAuthorizationRequestRepository)
                    .and()
                    .redirectionEndpoint()
                    .baseUri("/*/oauth2/code/*")
                    .and()
                    .userInfoEndpoint()
                    .userService(oAuth2UserService)
                    .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler);

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
