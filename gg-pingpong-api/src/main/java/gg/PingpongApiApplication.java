package gg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"gg.admin.repo", "gg.data", "gg.repo",
	"gg.pingpong.api", "gg.utils", "gg.party.api", "gg.auth"})
public class PingpongApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PingpongApiApplication.class, args);
	}

}
