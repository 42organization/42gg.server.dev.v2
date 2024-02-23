package gg.pingpong.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"gg.pingpong.admin.repo", "gg.pingpong.data", "gg.pingpong.repo",
	"gg.pingpong.api", "gg.pingpong.utils"})
public class PingpongApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PingpongApiApplication.class, args);
	}

}
