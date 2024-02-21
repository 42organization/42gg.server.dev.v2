package gg.pingpong.api.global.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"gg.pingpong.repo", "gg.pingpong.admin.repo"})
@EntityScan(basePackages = "gg.pingpong.data")
public class JpaConfig {
}
