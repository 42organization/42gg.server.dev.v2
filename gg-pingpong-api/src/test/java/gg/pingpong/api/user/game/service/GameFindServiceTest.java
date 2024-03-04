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

import gg.data.game.Game;
import gg.data.game.type.Mode;
import gg.data.game.type.StatusType;
import gg.data.rank.Tier;
import gg.data.rank.redis.RankRedis;
import gg.data.season.Season;
import gg.data.user.User;
import gg.auth.utils.AuthTokenProvider;
import gg.pingpong.api.user.game.controller.response.GameListResDto;
import gg.pingpong.api.user.game.controller.response.GameResultResDto;
import gg.repo.game.GameRepository;
import gg.repo.game.out.GameTeamUser;
import gg.repo.rank.TierRepository;
import gg.repo.rank.redis.RankRedisRepository;
import gg.utils.RedisKeyManager;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.exception.tier.TierNotFoundException;
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
