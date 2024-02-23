package gg.pingpong.api.global.utils;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Setter;

@Configuration
@Setter
@ConfigurationProperties(prefix = "info")
public class ApplicationYmlRead {
	private Map<String, String> web;

	public String getFrontUrl() {
		return web.get("frontUrl");
	}

	public String getDomain() {
		return web.get("domain");
	}

	public String getFrontLoginUrl() {
		return web.get("frontUrl") + "/login";
	}
}
