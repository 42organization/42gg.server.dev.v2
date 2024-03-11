package gg.pingpong.api.user.match.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gg.pingpong.api.user.manage.service.PenaltyService;
import gg.pingpong.api.user.season.service.SeasonFindService;
import gg.repo.game.GameRepository;
import gg.repo.manage.SlotManagementRepository;
import gg.repo.match.RedisMatchTimeRepository;
import gg.repo.match.RedisMatchUserRepository;
import gg.repo.rank.redis.RankRedisRepository;
import gg.repo.tournarment.TournamentRepository;
import gg.repo.user.UserRepository;
import gg.utils.annotation.UnitTest;

@UnitTest
public class MatchServiceUnitTest {
	@InjectMocks
	private MatchService matchService;
	@Mock
	private RedisMatchTimeRepository redisMatchTimeRepository;
	@Mock
	private RedisMatchUserRepository redisMatchUserRepository;
	@Mock
	private SeasonFindService seasonFindService;
	@Mock
	private RankRedisRepository rankRedisRepository;
	@Mock
	private GameRepository gameRepository;
	@Mock
	private PenaltyService penaltyService;
	@Mock
	private GameUpdateService gameUpdateService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private TournamentRepository tournamentRepository;
	@Mock
	private SlotManagementRepository slotManagementRepository;

	/**
	 * makeMatch
	 */
	@Test
	@DisplayName("")
	void makeMatch() {

	}

	/**
	 * cancelMatch
	 */
	@Test
	@DisplayName("")
	void cancelMatch() {

	}
}
