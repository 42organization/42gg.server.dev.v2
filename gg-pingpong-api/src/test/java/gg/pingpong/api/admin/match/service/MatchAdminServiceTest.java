package gg.pingpong.api.admin.match.service;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import gg.data.pingpong.match.RedisMatchUser;
import gg.data.pingpong.match.type.MatchKey;
import gg.data.pingpong.match.type.Option;
import gg.data.user.User;
import gg.pingpong.api.admin.match.service.dto.MatchUser;
import gg.pingpong.api.user.match.utils.MatchIntegrationTestUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@Transactional
class MatchAdminServiceTest {
	@Autowired
	private MatchAdminService matchAdminService;
	@Autowired
	private MatchIntegrationTestUtils matchTestSetting;
	@Autowired
	private RedisTemplate<String, RedisMatchUser> redisTemplate;

	@Test
	@DisplayName("매칭큐에 2명의 유저가 있고 BOTH 옵션으로 조회하면 2명의 유저가 조회된다.")
	void getMatches() {
		// given
		User user1 = matchTestSetting.createUser();
		User user2 = matchTestSetting.createUser();
		LocalDateTime startTime = LocalDateTime.of(2021, 1, 1, 0, 0);
		RedisMatchUser redisMatchUser1 = new RedisMatchUser(user1.getId(), 123, Option.BOTH);
		RedisMatchUser redisMatchUser2 = new RedisMatchUser(user2.getId(), 100, Option.RANK);
		redisTemplate.opsForList().rightPush(MatchKey.getTime(startTime), redisMatchUser1);
		redisTemplate.opsForList().rightPush(MatchKey.getTime(startTime), redisMatchUser2);

		// when
		Map<LocalDateTime, List<MatchUser>> matches = matchAdminService.getMatches(Option.BOTH);

		// then
		assertEquals(2, matches.get(startTime).size());
	}
}
