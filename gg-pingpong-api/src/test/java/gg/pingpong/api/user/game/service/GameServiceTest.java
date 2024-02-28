package gg.pingpong.api.user.game.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import gg.data.game.Game;
import gg.data.game.Team;
import gg.data.game.TeamUser;
import gg.data.game.type.Mode;
import gg.data.game.type.StatusType;
import gg.data.rank.Rank;
import gg.data.rank.Tier;
import gg.data.rank.redis.RankRedis;
import gg.data.season.Season;
import gg.data.user.User;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.user.game.controller.request.RankResultReqDto;
import gg.repo.game.GameRepository;
import gg.repo.game.TeamRepository;
import gg.repo.game.TeamUserRepository;
import gg.repo.rank.RankRepository;
import gg.repo.rank.TierRepository;
import gg.repo.rank.redis.RankRedisRepository;
import gg.utils.RedisKeyManager;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.exception.rank.RankNotFoundException;
import gg.utils.exception.tier.TierNotFoundException;
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
