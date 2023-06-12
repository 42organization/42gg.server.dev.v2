package com.gg.server.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.game.service.GameFindService;
import com.gg.server.domain.game.service.GameService;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.GameTeamInfo;
import com.gg.server.domain.game.dto.req.RankResultReqDto;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RankRedisService;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.team.data.TeamRepository;
import com.gg.server.domain.team.data.TeamUser;
import com.gg.server.domain.team.data.TeamUserRepository;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class GameControllerTest {
    @Autowired
    GameRepository gameRepository;
    @Autowired
    SeasonRepository seasonRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TeamUserRepository teamUserRepository;
    @Autowired
    RankRedisRepository rankRedisRepository;
    @Autowired
    PChangeRepository pChangeRepository;
    @Autowired
    RankRedisService rankRedisService;
    @Autowired
    RankRepository rankRepository;
    @Autowired
    TestDataUtils testDataUtils;
    @Autowired
    GameService gameService;
    @Autowired
    GameFindService gameFindService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AuthTokenProvider tokenProvider;
    private String accessToken;
    private Season season;
    private User user1;
    private User user2;
    private Game game1;
    private Game game2;

    @BeforeEach
    void init() {
        season = seasonRepository.save(new Season("test season", LocalDateTime.of(2023, 5, 14, 0, 0), LocalDateTime.of(2999, 12, 31, 23, 59),
                1000, 100));
        user1 = testDataUtils.createNewUser("test1", "test1@email", "null1", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
        accessToken = tokenProvider.createToken(user1.getId());
        user2 = testDataUtils.createNewUser("test2", "test2@email", "null1", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
        rankRepository.save(Rank.from(user1, season, season.getStartPpp()));
        rankRepository.save(Rank.from(user2, season, season.getStartPpp()));
        RankRedis userRank = RankRedis.from(UserDto.from(user1), season.getStartPpp());
        String redisHashKey = RedisKeyManager.getHashKey(season.getId());
        rankRedisRepository.addRankData(redisHashKey, user1.getId(), userRank);
        userRank = RankRedis.from(UserDto.from(user2), season.getStartPpp());
        rankRedisRepository.addRankData(redisHashKey, user2.getId(), userRank);
        for (int i = 0; i < 10; i++) {
            Game game = gameRepository.save(new Game(season, StatusType.WAIT, Mode.RANK, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
            Team team1 = teamRepository.save(new Team(game, 1, false));
            Team team2 = teamRepository.save(new Team(game, 2, true));
            List<TeamUser> teams = new ArrayList<>();
            teams.add(teamUserRepository.save(new TeamUser(team1, user1)));
            teams.add(teamUserRepository.save(new TeamUser(team2, user2)));
            gameService.expUpdates(game, teams);
            rankRedisService.updateRankRedis(teams.get(0), teams.get(1), game);
            game = gameRepository.save(new Game(season, StatusType.WAIT, Mode.NORMAL, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
            team1 = teamRepository.save(new Team(game, 0, false));
            team2 = teamRepository.save(new Team(game, 0, false));
            teamUserRepository.save(new TeamUser(team1, user1));
            teamUserRepository.save(new TeamUser(team2, user2));
            teams.clear();
            teams.add(teamUserRepository.save(new TeamUser(team1, user1)));
            teams.add(teamUserRepository.save(new TeamUser(team2, user2)));
            game.updateStatus();
            gameService.expUpdates(game, teams);
            pChangeRepository.save(new PChange(game, user1, 0, true));
            pChangeRepository.save(new PChange(game, user2, 0, true));
        }
        game1 = gameRepository.save(new Game(season, StatusType.WAIT, Mode.RANK, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
        Team team1 = teamRepository.save(new Team(game1, 1, false));
        Team team2 = teamRepository.save(new Team(game1, 2, true));
        teamUserRepository.save(new TeamUser(team1, user1));
        teamUserRepository.save(new TeamUser(team2, user2));
        game2 = gameRepository.save(new Game(season, StatusType.WAIT, Mode.RANK, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
        team1 = teamRepository.save(new Team(game1, 1, false));
        team2 = teamRepository.save(new Team(game1, 2, true));
        List<TeamUser> teams = new ArrayList<>();
        teams.add(teamUserRepository.save(new TeamUser(team1, user1)));
        teams.add(teamUserRepository.save(new TeamUser(team2, user2)));
        gameService.expUpdates(game2, teams);
        rankRedisService.updateRankRedis(teams.get(0), teams.get(1), game2);
    }

    @AfterEach
    public void flushRedis() {
        rankRedisRepository.deleteAll();
    }

    @Test
    @Transactional
    void 유저게임정보조회테스트() throws Exception {
        //given
        String url = "/pingpong/games/" + game1.getId().toString();
        GameTeamInfo expect = gameService.getUserGameInfo(game1.getId(), user1.getId());
        // when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        GameTeamInfo result = objectMapper.readValue(contentAsString, GameTeamInfo.class);
        System.out.println("expect: " + expect);
        System.out.println("result: " + result);
        assertThat(result.getGameId()).isEqualTo(expect.getGameId());
        assertThat(result.getStartTime()).isEqualTo(expect.getStartTime());
        assertThat(result.getMatchTeamsInfo().getMyTeam().getTeamId()).isEqualTo(expect.getMatchTeamsInfo().getMyTeam().getTeamId());
        assertThat(result.getMatchTeamsInfo().getEnemyTeam().getTeamId()).isEqualTo(expect.getMatchTeamsInfo().getEnemyTeam().getTeamId());
    }

    @Test
    @Transactional
    public void 일반게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games/normal?page=1&size=10";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"));
        GameListResDto expect = gameFindService.getNormalGameList(pageable);
        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
        GameListResDto result = objectMapper.readValue(contentAsString, GameListResDto.class);
        //then
        System.out.println(result.getGames().size() +", " + result.getIsLast());
        System.out.println(expect.getGames());
        assertThat(result.getGames().size()).isEqualTo(expect.getGames().size());
        assertThat(result.getGames().get(0).getGameId().equals(expect.getGames().get(0).getGameId()));
        assertThat(result.getIsLast()).isEqualTo(expect.getIsLast());
    }

    @Test
    @Transactional
    public void user일반게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games/normal?page=1&size=10&intraId=test1";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"));
        GameListResDto expect = gameFindService.normalGameListByIntra(pageable, "test1");
        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
        GameListResDto result = objectMapper.readValue(contentAsString, GameListResDto.class);
        //then
        System.out.println(result.getGames().size() +", " + result.getIsLast());
        System.out.println(expect.getGames());
        assertThat(result.getGames().size()).isEqualTo(expect.getGames().size());
        assertThat(result.getGames().get(0).getGameId().equals(expect.getGames().get(0).getGameId()));
        assertThat(result.getIsLast()).isEqualTo(expect.getIsLast());
    }

    @Test
    @Transactional
    public void 랭크게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games/rank?page=1&size=10&seasonId=" + season.getId();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"));
        GameListResDto expect = gameFindService.rankGameList(pageable, season.getId());
        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
        GameListResDto result = objectMapper.readValue(contentAsString, GameListResDto.class);
        //then
        System.out.println(result.getGames().size() +", " + result.getIsLast());
        System.out.println(expect.getGames().size() + ", " + expect.getIsLast());
        assertThat(result.getGames().size()).isEqualTo(expect.getGames().size());
        assertThat(result.getGames().get(0).getGameId().equals(expect.getGames().get(0).getGameId()));
        assertThat(result.getIsLast()).isEqualTo(expect.getIsLast());
    }

    @Test
    @Transactional
    public void user랭크게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games/rank?page=1&size=10&seasonId=" + season.getId() + "&nickname=" + "test1";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"));
        GameListResDto expect = gameFindService.rankGameListByIntra(pageable, season.getId(), "test1");
        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
        GameListResDto result = objectMapper.readValue(contentAsString, GameListResDto.class);
        //then
        System.out.println(result.getGames().size() +", " + result.getIsLast());
        System.out.println(expect.getGames().size() + ", " + expect.getIsLast());
        assertThat(result.getGames().size()).isEqualTo(expect.getGames().size());
        assertThat(result.getGames().get(0).getGameId().equals(expect.getGames().get(0).getGameId()));
        assertThat(result.getIsLast()).isEqualTo(expect.getIsLast());
    }

    @Test
    @Transactional
    public void 랭크게임목록error조회() throws Exception {
        //given
        String url = "/pingpong/games/rank?page=1&size=0";
        //then
        mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
    }
    @Test
    @Transactional
    public void 전체게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games?page=1&size=10";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"));
        GameListResDto expect = gameFindService.allGameList(pageable, null);
        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
        GameListResDto result = objectMapper.readValue(contentAsString, GameListResDto.class);
        //then
        System.out.println(result.getGames().size() +", " + result.getIsLast());
        System.out.println(expect.getGames().size() + ", " + expect.getIsLast());
        assertThat(result.getGames().size()).isEqualTo(expect.getGames().size());
        assertThat(result.getGames().get(result.getGames().size() - 1).getGameId().equals(expect.getGames().get(expect.getGames().size() - 1).getGameId()));
        assertThat(result.getIsLast()).isEqualTo(expect.getIsLast());
    }

    @Test
    @Transactional
    public void user전체게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games?page=1&size=10&nickname=test1";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"));
        GameListResDto expect = gameFindService.allGameListUser(pageable, "test1", null);
        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(contentAsString);
        GameListResDto result = objectMapper.readValue(contentAsString, GameListResDto.class);
        //then
        System.out.println(result.getGames().size() +", " + result.getIsLast());
        System.out.println(expect.getGames().size() + ", " + expect.getIsLast());
        assertThat(result.getGames().size()).isEqualTo(expect.getGames().size());
        assertThat(result.getGames().get(result.getGames().size() - 1).getGameId()).isEqualTo(expect.getGames().get(expect.getGames().size() - 1).getGameId());
        assertThat(result.getIsLast()).isEqualTo(expect.getIsLast());
    }

    @Test
    @Transactional
    void 게임목록조회에러테스트() throws Exception {
        String url = "/pingpong/games?pageNum=1&pageSize=10&status=live";
        mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }
    @Test
    @Transactional
    void 게임목록조회에러테스트2() throws Exception {
        String url = "/pingpong/games?page=1&size=10&status=2";
        mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @Transactional
    void 랭크게임결과입력테스트() throws Exception {
        String url = "/pingpong/games/rank";
        Game game = gameRepository.save(new Game(season, StatusType.WAIT, Mode.RANK, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
        Team team1 = teamRepository.save(new Team(game, -1, false));
        Team team2 = teamRepository.save(new Team(game, -1, false));
        String ac1 = tokenProvider.createToken(user1.getId());
        String ac2 = tokenProvider.createToken(user2.getId());
        teamUserRepository.save(new TeamUser(team1, user1));
        teamUserRepository.save(new TeamUser(team2, user2));
        teamUserRepository.flush();
        gameRepository.flush();
        teamRepository.flush();
        String content = objectMapper.writeValueAsString(new RankResultReqDto(game.getId(), team1.getId(), 1, team2.getId(), 2));
        System.out.println(user1.getTotalExp());
        System.out.println(user2.getTotalExp());
        // then
        System.out.println("=======================");
        mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        System.out.println("=======================");
        content = objectMapper.writeValueAsString(new RankResultReqDto(game.getId(), team2.getId(), 2, team1.getId(), 1));
        mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + ac2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict())
                .andReturn().getResponse();
        System.out.println(user1.getTotalExp());
        System.out.println(user2.getTotalExp());
    }

    @Test
    @Transactional
    void 랭크게임결과조회() throws Exception {
        String url = "/pingpong/games/" + game2.getId() + "/result/rank";
        String content = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println("result: " + content);
    }
}
