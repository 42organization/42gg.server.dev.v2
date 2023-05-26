package com.gg.server.game.service;


import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.service.GameFindService;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GameDBTest {

    @Autowired
    GameFindService gameFindService;
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    AuthTokenProvider tokenProvider;
    @Autowired
    RankRedisRepository rankRedisRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TeamUserRepository teamUserRepository;
    @Autowired
    PChangeRepository pChangeRepository;
    @Autowired
    EntityManager em;

    @BeforeEach
    void init() {
        Season season = testDataUtils.createSeason();
        User newUser = testDataUtils.createNewUser();
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
        RankRedis userRank = RankRedis.from(UserDto.from(newUser), season.getStartPpp());
        String redisHashKey = RedisKeyManager.getHashKey(season.getId());
        rankRedisRepository.addRankData(redisHashKey, newUser.getId(), userRank);

//        for (int i = 0; i < 10; i++) {
//            Game game = gameRepository.save(new Game(season, StatusType.WAIT, Mode.RANK, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
//            Team team1 = teamRepository.save(new Team(game, 1, false));
//            Team team2 = teamRepository.save(new Team(game, 2, true));
//            List<TeamUser> teams = new ArrayList<>();
//            teams.add(teamUserRepository.save(new TeamUser(team1, user1)));
//            teams.add(teamUserRepository.save(new TeamUser(team2, user2)));
//            game.updateStatus();
//            rankRedisService.updateRankRedis(teams, season.getId(), game);
//            game = gameRepository.save(new Game(season, StatusType.WAIT, Mode.NORMAL, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
//            team1 = teamRepository.save(new Team(game, 0, false));
//            team2 = teamRepository.save(new Team(game, 0, false));
//            teamUserRepository.save(new TeamUser(team1, user1));
//            teamUserRepository.save(new TeamUser(team2, user2));
//            teams.clear();
//            teams.add(teamUserRepository.save(new TeamUser(team1, user1)));
//            teams.add(teamUserRepository.save(new TeamUser(team2, user2)));
//            game.updateStatus();
//            gameService.expUpdates(game, teams);
//            pChangeRepository.save(new PChange(game, user1, 0));
//            pChangeRepository.save(new PChange(game, user2, 0));
//        }
//        game1 = gameRepository.save(new Game(season, StatusType.WAIT, Mode.RANK, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
//        Team team1 = teamRepository.save(new Team(game1, 1, false));
//        Team team2 = teamRepository.save(new Team(game1, 2, true));
//        teamUserRepository.save(new TeamUser(team1, user1));
//        teamUserRepository.save(new TeamUser(team2, user2));
    }
    @Test
    @DisplayName(value = "Cascade 종속삭제테스트")
    @Transactional
    public void Cascade종속삭제테스트() throws Exception {
        pChangeRepository.deleteAll();
        gameRepository.deleteAll();
        em.flush();
        List<Game> gameList = gameRepository.findAll();
        List<Team> teamList = teamRepository.findAll();
        List<TeamUser> teamUserList = teamUserRepository.findAll();
        log.info("GAME LIST SIZE : " + Integer.toString(gameList.size()));
        log.info("TEAM LIST SIZE: " + Integer.toString(teamList.size()));
        log.info("TEAM_USER LIST SIZE: " + Integer.toString(teamUserList.size()));
        Assertions.assertThat(teamList.size()).isEqualTo(0);
        Assertions.assertThat(teamUserList.size()).isEqualTo(0);
    }
}
