package gg.pingpong.utils.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

public class RedisInitializer implements
	ApplicationContextInitializer<ConfigurableApplicationContext> {
	private static final RedisContainer REDIS_CONTAINER =
		new RedisContainer(DockerImageName.parse("redis:5.0.3-alpine"))
			.withExposedPorts(6379);

	static {
		REDIS_CONTAINER.start();
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		TestPropertyValues.of(
			"spring.redis.host=" + REDIS_CONTAINER.getHost(),
			"spring.redis.port=" + REDIS_CONTAINER.getMappedPort(6379)
		).applyTo(applicationContext.getEnvironment());
	}
}
