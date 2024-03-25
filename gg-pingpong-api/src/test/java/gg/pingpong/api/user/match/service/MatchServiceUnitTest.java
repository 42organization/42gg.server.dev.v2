package gg.pingpong.api.user.match.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import gg.auth.UserDto;
import gg.data.game.type.StatusType;
import gg.data.manage.SlotManagement;
import gg.data.match.RedisMatchUser;
import gg.data.match.type.Option;
import gg.data.rank.redis.RankRedis;
import gg.data.season.Season;
import gg.data.tournament.type.TournamentStatus;
import gg.pingpong.api.user.manage.service.PenaltyService;
import gg.pingpong.api.user.match.dto.GameAddDto;
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
	@Nested
	@DisplayName("makeMatch() : 매칭큐에 RANK 옵션으로 신청한 유저 1명 있을 때")
	class MatchTest {
		UserDto userDto;
		LocalDateTime startTime;
		Season season;
		RankRedis rank;
		List<RedisMatchUser> allMatchUsers;

		@BeforeEach
		void setUp() {
			userDto = Mockito.mock(UserDto.class);
			startTime = LocalDateTime.of(2021, 1, 1, 0, 0);
			season = Season.builder().pppGap(0).build();
			rank = Mockito.mock(RankRedis.class);
			allMatchUsers = List.of(new RedisMatchUser(2L, 1000, Option.RANK));

			// checkValid
			given(penaltyService.isPenaltyUser(userDto.getIntraId())).willReturn(false);
			given(tournamentRepository.findAllByStatusIsNot(TournamentStatus.END)).willReturn(List.of());
			SlotManagement slotManagment = SlotManagement.builder().gameInterval(15).build();
			given(slotManagementRepository.findCurrent(any(LocalDateTime.class))).willReturn(
				Optional.of(slotManagment));
			given(gameRepository.findByStartTime(any(LocalDateTime.class))).willReturn(Optional.empty());
			given(gameRepository.findByStatusTypeAndUserId(any(StatusType.class), anyLong())).willReturn(
				Optional.empty());
			given(redisMatchUserRepository.getUserTime(anyLong(), any(LocalDateTime.class))).willReturn(
				Optional.empty());
			given(redisMatchUserRepository.countMatchTime(anyLong())).willReturn(0);

			// makeMatch
			given(seasonFindService.findCurrentSeason(startTime)).willReturn(season);
			given(rankRedisRepository.findRankByUserId(anyString(), anyLong())).willReturn(rank);
			given(redisMatchTimeRepository.getAllMatchUsers(startTime)).willReturn(allMatchUsers);
		}

		@Test
		@DisplayName("BOTH로 매칭 신청했을 때 season의 pppGap 차이로 매칭이 안 되고 매칭큐에 넣어준다.")
		void addQueue1() {
			// when
			matchService.makeMatch(userDto, Option.BOTH, startTime);

			// then
			verify(redisMatchTimeRepository, times(1)).addMatchUser(any(LocalDateTime.class),
				any(RedisMatchUser.class));
			verify(redisMatchTimeRepository, times(1)).setMatchTimeWithExpiry(startTime);
			verify(redisMatchUserRepository, times(1)).addMatchTime(anyLong(), any(LocalDateTime.class),
				any(Option.class));
		}

		@Test
		@DisplayName("BOTH로 매칭 신청했을 때 게임이 매칭된다.")
		void makeMatch() {
			// given
			season.setPppGap(2000);
			given(seasonFindService.findCurrentSeason(startTime)).willReturn(season);

			// when
			matchService.makeMatch(userDto, Option.BOTH, startTime);

			// then
			verify(gameUpdateService, times(1)).make(any(GameAddDto.class), anyLong());
			verify(redisMatchTimeRepository, times(1)).addMatchUser(any(LocalDateTime.class),
				any(RedisMatchUser.class));
		}

		@Test
		@DisplayName("NORMAL로 신청하면 유저를 매칭큐에 넣어준다.")
		void putQueue() {
			// when
			matchService.makeMatch(userDto, Option.NORMAL, startTime);

			// then
			verify(redisMatchTimeRepository, times(1)).addMatchUser(any(LocalDateTime.class),
				any(RedisMatchUser.class));
			verify(redisMatchTimeRepository, times(1)).setMatchTimeWithExpiry(startTime);
			verify(redisMatchUserRepository, times(1)).addMatchTime(anyLong(), any(LocalDateTime.class),
				any(Option.class));
		}

	}
}
