package gg.pingpong.api.user.game.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.user.game.controller.response.GameListResDto;
import gg.pingpong.api.user.game.controller.response.GameResultResDto;
import gg.pingpong.data.game.Game;
import gg.pingpong.data.game.Season;
import gg.pingpong.data.game.Tier;
import gg.pingpong.data.game.redis.RankRedis;
import gg.pingpong.data.game.type.Mode;
import gg.pingpong.data.game.type.StatusType;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.game.GameRepository;
import gg.pingpong.repo.game.GameTeamUser;
import gg.pingpong.repo.rank.redis.RankRedisRepository;
import gg.pingpong.repo.tier.TierRepository;
import gg.pingpong.utils.RedisKeyManager;
import gg.pingpong.utils.TestDataUtils;
import gg.pingpong.utils.annotation.IntegrationTest;
import gg.pingpong.utils.exception.tier.TierNotFoundException;
import lombok.RequiredArgsConstructor;

@IntegrationTest
@RequiredArgsConstructor
@Transactional
public class GameFindServiceTest {

	@Autowired
	GameFindService gameFindService;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	AuthTokenProvider tokenProvider;
	@Autowired
	RankRedisRepository rankRedisRepository;

	@Autowired
	TierRepository tierRepository;

	@Autowired
	GameRepository gameRepository;

	@BeforeEach
	void init() {
		testDataUtils.createTierSystem("pingpong");
		Season season = testDataUtils.createSeason();
		User newUser = testDataUtils.createNewUser();
		Tier tier = tierRepository.findStartTier().orElseThrow(TierNotFoundException::new);
		String accessToken = tokenProvider.createToken(newUser.getId());
		String statusMsg = "status message test1";

		LocalDateTime startTime = LocalDateTime.now().minusDays(1);
		LocalDateTime endTime = startTime.plusMinutes(15);
		testDataUtils.createMockMatch(newUser, season, startTime, endTime);

		LocalDateTime startTime1 = LocalDateTime.now().minusDays(2);
		LocalDateTime endTime1 = startTime1.plusMinutes(15);
		testDataUtils.createMockMatch(newUser, season, startTime1, endTime1);

		LocalDateTime startTime2 = LocalDateTime.now().minusDays(3);
		LocalDateTime endTime2 = startTime2.plusMinutes(15);
		testDataUtils.createMockMatch(newUser, season, startTime2, endTime2);

		testDataUtils.createUserRank(newUser, statusMsg, season);
		RankRedis userRank = RankRedis.from(newUser.getId(), newUser.getIntraId(), newUser.getTextColor(),
			season.getStartPpp(), tier.getImageUri());
		String redisHashKey = RedisKeyManager.getHashKey(season.getId());
		rankRedisRepository.addRankData(redisHashKey, newUser.getId(), userRank);
	}

	@AfterEach
	public void flushRedis() {
		rankRedisRepository.deleteAll();
	}

	@Test
	void normalGameListGet() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"));
		GameListResDto response = gameFindService.getNormalGameList(pageable);
		Slice<Game> games = gameRepository.findAllByModeAndStatus(Mode.NORMAL, StatusType.END, pageable);
		GameListResDto expect = new GameListResDto(
			getGameResultList(games.getContent().stream().map(Game::getId).collect(Collectors.toList())),
			games.isLast());
		assertThat(response).isEqualTo(expect);
	}

	private List<GameResultResDto> getGameResultList(List<Long> games) {
		List<GameTeamUser> teamViews = gameRepository.findTeamsByGameIsIn(games);
		return teamViews.stream().map(GameResultResDto::new).collect(Collectors.toList());
	}

}
