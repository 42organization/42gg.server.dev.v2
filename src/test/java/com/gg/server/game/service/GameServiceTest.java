package com.gg.server.game.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.service.GameStatusService;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.scheduler.GameStatusScheduler;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@RequiredArgsConstructor
public class GameServiceTest {
    @Autowired
    private GameRepository gameRepository;
    @Autowired private SeasonRepository seasonRepository;
    @Autowired
    private GameStatusService gameStatusService;
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamUserRepository teamUserRepository;
    private Season season;
    User user1;
    User user2;
    Game game1;
    Game liveGame;

    @BeforeEach
    void init() {
        season = seasonRepository.save(new Season("test season", LocalDateTime.of(2023, 5, 14, 0, 0), LocalDateTime.of(2999, 12, 31, 23, 59),
                1000, 100));
        user1 = testDataUtils.createNewUser("test2", "test2@naver.com", "null1", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
        user2 = testDataUtils.createNewUser("test3", "test3@naver.com", "null1", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute());
        game1 = gameRepository.save(new Game(season, StatusType.BEFORE, Mode.RANK, startTime, startTime.plusMinutes(15)));
        Team team1 = teamRepository.save(new Team(game1, 1, false));
        Team team2 = teamRepository.save(new Team(game1, 2, true));
        teamUserRepository.save(new TeamUser(team1, user1));
        teamUserRepository.save(new TeamUser(team2, user2));
        liveGame = gameRepository.save(new Game(season, StatusType.LIVE, Mode.RANK, startTime.minusMinutes(15), startTime));
    }
    @Test
    @Transactional
    void gameBefore상태변경테스트() throws Exception{
        System.out.println("g1.startTime: " + game1.getStartTime());
        System.out.println(game1.getStatus());
        gameStatusService.updateBeforeToLiveStatus();
        assertThat(game1.getStatus()).isEqualTo(StatusType.LIVE);
    }

    @Test
    @Transactional
    void gameLIVE상태변경테스트() throws Exception{
        gameStatusService.updateLiveToWaitStatus();
        assertThat(liveGame.getStatus()).isEqualTo(StatusType.WAIT);
    }

    @Test
    @Transactional
    void game5분전알림테스트() throws Exception{
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute());
        System.out.println(startTime.plusMinutes(5));
        Game game = gameRepository.save(new Game(season, StatusType.BEFORE, Mode.RANK, startTime.plusMinutes(5), startTime.plusMinutes(20)));
        Team team1 = teamRepository.save(new Team(game, 0, false));
        Team team2 = teamRepository.save(new Team(game, 0, true));
        teamUserRepository.save(new TeamUser(team1, user1));
        teamUserRepository.save(new TeamUser(team2, user2));
        gameRepository.flush();
        teamRepository.flush();
        teamUserRepository.flush();
        System.out.println("==============");
        gameStatusService.imminentGame();
    }
}
