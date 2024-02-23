package gg.pingpong.api.global.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Redis 설정
 */
@Configuration
@EnableCaching
@EnableRedisRepositories
@EnableTransactionManagement
public class RedisConfig {

	/**
	 * Redis Host
	 */
	@Value("${spring.redis.host}")
	private String host;

	/**
	 * Redis Port
	 */
	@Value("${spring.redis.port}")
	private int port;

	/**
	 * Redis Connection Factory
	 * <p>
	 * redisConnectionFactory() 메소드를 통해 RedisConnectionFactory 를 생성하고, 이를 통해 RedisTemplate 를 생성한다. 해당
	 * 기능은 Spring Boot 에서 자동으로 제공해주지만, RedisConnectionFactory 를 커스터마이징 하기 위해 직접 생성
	 *
	 * @return
	 */
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}

	/**
	 * 캐시 관리자
	 * <p>
	 * RedisCacheManager 를 통해 Redis 에 대한 캐시를 관리한다. 캐시의 기본 설정을 변경하기 위해 RedisCacheConfiguration 을 사용한다.
	 *
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public CacheManager gameCacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory());
		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
				new GenericJackson2JsonRedisSerializer())) // Value Serializer 변경
			.entryTtl(Duration.ofMinutes(30)); // 캐시 수명
		builder.cacheDefaults(configuration);
		return builder.build();
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

		redisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
		redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		redisTemplate.setEnableTransactionSupport(true); // <=

		return redisTemplate;
	}

	/**
	 * StringRedisTemplate
	 * <p>
	 * StringRedisTemplate 을 통해 Redis 에 데이터를 저장한다.
	 *
	 * @param redisTemplate
	 * @return
	 */
	@Bean
	public ListOperations<?, ?> redisListTemplate(RedisTemplate<?, ?> redisTemplate) {
		return redisTemplate.opsForList();
	}

	/**
	 * ConfigureRedisAction
	 * <p>
	 * Elasticache를 사용할 때, CONFIG 명령어 사용이 제한됨으로 인해 발생하는 에러를 방지하기 위해 사용
	 *
	 * @return ConfigureRedisAction
	 */
	@Bean
	public ConfigureRedisAction configureRedisAction() {
		return ConfigureRedisAction.NO_OP;
	}

}
