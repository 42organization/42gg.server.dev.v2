package gg.pingpong.api.global.security.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
	private String allowedOrigins;
	private String allowedMethods;
	private String allowedHeaders;
	private Long maxAge;
}
