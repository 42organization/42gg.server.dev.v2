package gg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"gg.admin.repo", "gg.data", "gg.repo",
	"gg.pingpong.api", "gg.utils", "gg.party.api", "gg.auth"})
@EnableConfigurationProperties({gg.auth.properties.AppProperties.class})
public class PingpongApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PingpongApiApplication.class, args);
	}

}
