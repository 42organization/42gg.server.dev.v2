package gg.pingpong.api.global.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"gg.repo", "gg.admin.repo"})
@EntityScan(basePackages = "gg.data")
public class JpaConfig {
}
