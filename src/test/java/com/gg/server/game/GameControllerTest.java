package com.gg.server.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.game.GameService;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.req.RankResultReqDto;
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
    TestDataUtils testDataUtils;
    @Autowired
    GameService gameService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AuthTokenProvider tokenProvider;
    private String accessToken;

    @BeforeEach
    public void init() {
        accessToken = testDataUtils.getLoginAccessToken();
    }

    @Test
    public void 일반게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games/normal?pageNum=0&pageSize=10";
        GameListResDto expect = gameService.normalGameList(0, 10);
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
        assertThat(result.getIsLast()).isEqualTo(expect.getIsLast());
    }

    @Test
    public void 랭크게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games/rank?pageNum=0&pageSize=10&seasonId=3";
        GameListResDto expect = gameService.normalGameList(0, 10);
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
        assertThat(result.getIsLast()).isEqualTo(expect.getIsLast());
    }

    @Test
    public void 랭크게임목록error조회() throws Exception {
        //given
        String url = "/pingpong/games/rank?pageNum=0&pageSize=0";
        //then
        mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
    }
    @Test
    public void 전체게임목록조회() throws Exception {
        //given
        String url = "/pingpong/games?pageNum=0&pageSize=10";
        GameListResDto expect = gameService.allGameList(0, 10, null);
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
        assertThat(result.getIsLast()).isEqualTo(expect.getIsLast());
    }

    @Test
    void 게임목록조회에러테스트() throws Exception {
        String url = "/pingpong/games?pageNum=0&pageSize=10&status=live";
        mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }
    @Test
    void 게임목록조회에러테스트2() throws Exception {
        String url = "/pingpong/games?pageNum=0&pageSize=10&status=2";
        mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void 랭크게임결과입력테스트() throws Exception {
        String url = "/pingpong/games/rank";
        // season 추가
        Season season = seasonRepository.getById(1L);
        Game game = gameRepository.save(new Game(season, StatusType.WAIT, Mode.RANK, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
        Team team1 = teamRepository.save(new Team(game, -1, false));
        Team team2 = teamRepository.save(new Team(game, -1, false));
        User user1 = testDataUtils.createNewUser("test1", "test1@email", "null1", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
        String ac1 = tokenProvider.createToken(user1.getId());
        User user2 = testDataUtils.createNewUser("test2", "test2@email", "null1", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
        String ac2 = tokenProvider.createToken(user2.getId());
        TeamUser tu1 = teamUserRepository.save(new TeamUser(team1, user1));
        TeamUser tu2 = teamUserRepository.save(new TeamUser(team2, user2));
        String content = objectMapper.writeValueAsString(new RankResultReqDto(game.getId(), team1.getId(), 1, team2.getId(), 2));
        mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        content = objectMapper.writeValueAsString(new RankResultReqDto(game.getId(), team2.getId(), 2, team1.getId(), 1));
        mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + ac2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
    }
}
