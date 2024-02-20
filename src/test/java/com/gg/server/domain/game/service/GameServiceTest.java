package com.gg.server.domain.game.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.data.game.Game;
import com.gg.server.data.game.Rank;
import com.gg.server.data.game.Season;
import com.gg.server.data.game.Team;
import com.gg.server.data.game.TeamUser;
import com.gg.server.data.game.Tier;
import com.gg.server.data.game.redis.RankRedis;
import com.gg.server.data.game.type.Mode;
import com.gg.server.data.game.type.StatusType;
import com.gg.server.data.user.User;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.request.RankResultReqDto;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.exception.RankNotFoundException;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.tier.data.TierRepository;
import com.gg.server.domain.tier.exception.TierNotFoundException;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import com.gg.server.utils.annotation.IntegrationTest;

import lombok.RequiredArgsConstructor;

@IntegrationTest
@RequiredArgsConstructor
@Transactional
public class GameServiceTest {

	@Autowired
	RankRedisRepository rankRedisRepository;

	@Autowired
	GameRepository gameRepository;
	@Autowired
	TeamRepository teamRepository;

	@Autowired
	TeamUserRepository teamUserRepository;

	@Autowired
	RankRepository rankRepository;

	@Autowired
	TierRepository tierRepository;

	@Autowired
	GameService gameService;

	User user1;
	User user2;
	Game game1;
	Team team1;
	Team team2;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	AuthTokenProvider tokenProvider;

	@BeforeEach
	void init() {
		testDataUtils.createTierSystem("pingpong");
		Season season = testDataUtils.createSeason();
		Tier tier = tierRepository.findStartTier().orElseThrow(TierNotFoundException::new);
		user1 = testDataUtils.createNewUser();
		user2 = testDataUtils.createNewUser();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startTime = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
			now.getHour(), now.getMinute());
		game1 = gameRepository.save(new Game(season, StatusType.LIVE, Mode.RANK, startTime, startTime.plusMinutes(15)));
		team1 = teamRepository.save(new Team(game1, -1, false));
		team2 = teamRepository.save(new Team(game1, -1, true));
		teamUserRepository.save(new TeamUser(team1, user1));
		teamUserRepository.save(new TeamUser(team2, user2));
		String statusMsg = "status message test1";

		testDataUtils.createUserRank(user1, statusMsg, season);
		RankRedis userRank = RankRedis.from(user1.getId(), user1.getIntraId(), user1.getTextColor(),
			season.getStartPpp(), tier.getImageUri());
		String redisHashKey = RedisKeyManager.getHashKey(season.getId());
		rankRedisRepository.addRankData(redisHashKey, user1.getId(), userRank);
		statusMsg = "status message test2";
		testDataUtils.createUserRank(user2, statusMsg, season);
		RankRedis userRank2 = RankRedis.from(user2.getId(), user2.getIntraId(), user2.getTextColor(),
			season.getStartPpp(), tier.getImageUri());
		rankRedisRepository.addRankData(redisHashKey, user2.getId(), userRank2);
	}

	@AfterEach
	public void flushRedis() {
		rankRedisRepository.deleteAll();
	}

	@Test
	void ppp_change_test() throws Exception {
		String key = RedisKeyManager.getHashKey(game1.getSeason().getId());
		Integer user1BeforePpp = rankRedisRepository.findRankByUserId(key, user1.getId())
			.getPpp();
		System.out.println("Before ppp: " + user1BeforePpp);
		assertThat(gameService.createRankResult(new RankResultReqDto(game1.getId(), team1.getId(),
			1, team2.getId(), 2), user1.getId())).isEqualTo(true);
		Integer user1AfterPpp = rankRedisRepository.findRankByUserId(key, user1.getId()).getPpp();
		System.out.println("After ppp: " + rankRedisRepository.findRankByUserId(key, user1.getId())
			.getPpp());
		Rank rank = rankRepository.findByUserIdAndSeasonId(user1.getId(), game1.getSeason().getId())
			.orElseThrow(RankNotFoundException::new);
		assertThat(rank.getPpp()).isEqualTo(user1AfterPpp);
		assertThat(user1BeforePpp).isGreaterThan(user1AfterPpp);
	}
}
