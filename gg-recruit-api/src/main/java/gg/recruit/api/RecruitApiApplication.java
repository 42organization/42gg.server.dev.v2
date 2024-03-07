package gg.recruit.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"gg.recruit.api", "gg.utils", "gg.data", "gg.repo", "gg.admin.repo"
	, "gg.auth"})
public class RecruitApiApplication {
}
