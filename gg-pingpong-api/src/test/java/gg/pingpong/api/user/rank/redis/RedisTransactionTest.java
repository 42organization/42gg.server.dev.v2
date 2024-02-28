package gg.pingpong.api.user.rank.redis;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import gg.utils.annotation.IntegrationTestWithRedisTransaction;

@IntegrationTestWithRedisTransaction
public class RedisTransactionTest {

	@Autowired
	RedisTestService redisTestService;

	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Test
	@DisplayName("transaction안에서 exception이 발생한다면 해당 transaction이 discord되는지 확인")
	public void rollbackTest() throws Exception {
		String key = "hello";

		Assertions.assertThatThrownBy(() -> {
			redisTestService.addStringWithError(key, "aaa");
		}).isInstanceOf(RuntimeException.class);

		Object result = redisTemplate.opsForValue().get(key);
		Assertions.assertThat(result).isNull();
	}

	@Test
	@DisplayName("transaction이 올바르게 exec를 실행시키는지 테스트")
	public void commitTest() throws Exception {
		String key = "hello1";
		String value = "aaa";

		redisTestService.addString(key, value);

		Object result = redisTemplate.opsForValue().get(key);
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.toString()).isEqualTo(value);
	}

	@Test
	@DisplayName("transaction안에서 새로 집어넣은 key에 대해서 조회시 null조회 test")
	@Transactional
	public void nullTest() throws Exception {
		String key = "test";
		String value = "value";

		redisTestService.addString(key, value);

		Object result = redisTestService.getFromString(key);
		Assertions.assertThat(result).isNull();
	}

	@Test
	@DisplayName("transaction안에서 기존에 존재하던 key에 대해서 조회시 not null test")
	public void nonNullTest() throws Exception {
		String key = "test";
		String value = "value";

		redisTestService.addString(key, value);

		Object result = redisTestService.getFromString(key);
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.toString()).isEqualTo(value);
	}

}

