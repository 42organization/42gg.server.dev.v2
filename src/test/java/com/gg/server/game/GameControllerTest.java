package com.gg.server.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.game.GameService;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.GameTeamInfo;
import com.gg.server.domain.game.dto.req.RankResultReqDto;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
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
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    TestDataUtils testDataUtils;
    @Autowired
    GameService gameService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AuthTokenProvider tokenProvider;
    @Autowired
    RankRepository rankRepository;
    private String accessToken;
    private Season season;
    private User user1;
    private User user2;
    private Game game1;

    @BeforeEach
    void init() {
        season = seasonRepository.save(new Season("test season", LocalDateTime.of(2023, 5, 14, 0, 0), LocalDateTime.of(2999, 12, 31, 23, 59),
                1000, 100));
        user1 = testDataUtils.createNewUser("test1", "test1@email", "null1", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
        accessToken = tokenProvider.createToken(user1.getId());
        user2 = testDataUtils.createNewUser("test2", "test2@email", "null1", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
        for (int i = 0; i < 10; i++) {
            Game game = gameRepository.save(new Game(season, StatusType.END, Mode.RANK, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
            Team team1 = teamRepository.save(new Team(game, 1, false));
            Team team2 = teamRepository.save(new Team(game, 2, true));
            teamUserRepository.save(new TeamUser(team1, user1));
            teamUserRepository.save(new TeamUser(team2, user2));
            game = gameRepository.save(new Game(season, StatusType.END, Mode.NORMAL, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
            team1 = teamRepository.save(new Team(game, 0, false));
            team2 = teamRepository.save(new Team(game, 0, false));
            teamUserRepository.save(new TeamUser(team1, user1));
            teamUserRepository.save(new TeamUser(team2, user2));
        }
        game1 = gameRepository.save(new Game(season, StatusType.WAIT, Mode.RANK, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
        Team team1 = teamRepository.save(new Team(game1, 1, false));
        Team team2 = teamRepository.save(new Team(game1, 2, true));
        teamUserRepository.save(new TeamUser(team1, user1));
        teamUserRepository.save(new TeamUser(team2, user2));
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
        String url = "/pingpong/games/normal?pageNum=1&pageSize=10";
        GameListResDto expect = gameService.normalGameList(0, 10, null);
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
        String url = "/pingpong/games/normal?pageNum=1&pageSize=10&nickname=test1";
        GameListResDto expect = gameService.normalGameList(0, 10, "test1");
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
        String url = "/pingpong/games/rank?pageNum=1&pageSize=10&seasonId=" + season.getId();
        GameListResDto expect = gameService.rankGameList(0, 10, season.getId(), null);
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
        String url = "/pingpong/games/rank?pageNum=1&pageSize=10&seasonId=" + season.getId() + "&nickname=" + "test1";
        GameListResDto expect = gameService.rankGameList(0, 10, season.getId(), "test1");
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
        String url = "/pingpong/games/rank?pageNum=1&pageSize=0";
        //then
        mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
    }
    @Test
    @Transactional
    public void 전체게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games?pageNum=1&pageSize=10";
        GameListResDto expect = gameService.allGameList(0, 10, null, null);
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
        String url = "/pingpong/games?pageNum=1&pageSize=10&nickname=test1";
        GameListResDto expect = gameService.allGameList(0, 10, null, "test1");
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
    void 게임목록조회에러테스트() throws Exception {
        String url = "/pingpong/games?pageNum=1&pageSize=10&status=live";
        mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }
    @Test
    @Transactional
    void 게임목록조회에러테스트2() throws Exception {
        String url = "/pingpong/games?pageNum=1&pageSize=10&status=2";
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
//        User user1 = testDataUtils.createNewUser("test3", "test3@email", "null1", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
        String ac1 = tokenProvider.createToken(user1.getId());
//        User user2 = testDataUtils.createNewUser("test4", "test4@email", "null1", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
        String ac2 = tokenProvider.createToken(user2.getId());
        teamUserRepository.save(new TeamUser(team1, user1));
        teamUserRepository.save(new TeamUser(team2, user2));
        rankRepository.save(new Rank(user1, season, season.getStartPpp(), 0, 0, ""));
        rankRepository.save(new Rank(user2, season, season.getStartPpp(), 0, 0, ""));
        String content = objectMapper.writeValueAsString(new RankResultReqDto(game.getId(), team1.getId(), 1, team2.getId(), 2));
        rankRedisRepository.addRankData(RedisKeyManager.getHashKey(season.getId()), user1.getId(),
                new RankRedis(user1.getId(), user1.getIntraId(), season.getStartPpp(), 0, 0,  "test user3"));
        rankRedisRepository.addRankData(RedisKeyManager.getHashKey(season.getId()), user2.getId(),
                new RankRedis(user2.getId(), user2.getIntraId(), season.getStartPpp(), 0, 0,  "test user4"));
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
                .andExpect(status().isCreated())
                .andReturn().getResponse();
    }
}
