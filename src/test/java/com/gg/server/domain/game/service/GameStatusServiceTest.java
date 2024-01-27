package com.gg.server.domain.game.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.slotmanagement.SlotManagement;
import com.gg.server.domain.slotmanagement.data.SlotManagementRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.utils.TestDataUtils;
import com.gg.server.utils.annotation.IntegrationTest;

import lombok.RequiredArgsConstructor;

@IntegrationTest
@RequiredArgsConstructor
@Transactional
public class GameStatusServiceTest {
	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private SeasonRepository seasonRepository;
	@Autowired
	private GameStatusService gameStatusService;
	@Autowired
	private RankRedisRepository rankRedisRepository;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	private TeamRepository teamRepository;
	@Autowired
	private TeamUserRepository teamUserRepository;
	@Autowired
	private SlotManagementRepository slotManagementRepository;
	private Season season;
	User user1;
	User user2;
	Game game1;
	Game liveGame;

	@BeforeEach
	void init() {
		season = seasonRepository.save(
			new Season("test season", LocalDateTime.of(2023, 5, 14, 0, 0), LocalDateTime.of(2999, 12, 31, 23, 59),
				1000, 100));
		user1 = testDataUtils.createNewUser("test2", "test2@naver.com", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
		user2 = testDataUtils.createNewUser("test3", "test3@naver.com", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startTime = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
			now.getHour(), now.getMinute());
		game1 = gameRepository.save(
			new Game(season, StatusType.BEFORE, Mode.RANK, startTime, startTime.plusMinutes(15)));
		Team team1 = teamRepository.save(new Team(game1, 1, false));
		Team team2 = teamRepository.save(new Team(game1, 2, true));
		teamUserRepository.save(new TeamUser(team1, user1));
		teamUserRepository.save(new TeamUser(team2, user2));
		liveGame = gameRepository.save(
			new Game(season, StatusType.LIVE, Mode.RANK, startTime.minusMinutes(15), startTime));
	}

	@AfterEach
	public void flushRedis() {
		rankRedisRepository.deleteAll();
	}

	@Test
	void gameBeforeStatusChange() throws Exception {
		System.out.println("g1.startTime: " + game1.getStartTime());
		System.out.println(game1.getStatus());
		gameStatusService.updateBeforeToLiveStatus();
		assertThat(game1.getStatus()).isEqualTo(StatusType.LIVE);
	}

	@Test
	void gameLiveStatusChange() throws Exception {
		gameStatusService.updateLiveToWaitStatus();
		assertThat(liveGame.getStatus()).isEqualTo(StatusType.WAIT);
	}

	@Test
	void game5BeforeNoti() throws Exception {
		SlotManagement slotManagement = SlotManagement.builder()
			.futureSlotTime(12)
			.pastSlotTime(0)
			.openMinute(5)
			.gameInterval(15)
			.startTime(LocalDateTime.now().minusMinutes(1))
			.build();
		slotManagementRepository.save(slotManagement);
		System.out.println("==============");
		gameStatusService.imminentGame();
	}
}
