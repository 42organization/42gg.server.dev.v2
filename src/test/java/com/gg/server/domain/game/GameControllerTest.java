package com.gg.server.domain.game;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameListResDto;
import com.gg.server.domain.game.dto.GameTeamInfo;
import com.gg.server.domain.game.dto.request.RankResultReqDto;
import com.gg.server.domain.game.dto.request.TournamentResultReqDto;
import com.gg.server.domain.game.service.GameFindService;
import com.gg.server.domain.game.service.GameService;
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
import com.gg.server.domain.tier.data.Tier;
import com.gg.server.domain.tier.data.TierRepository;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.data.TournamentRepository;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import com.gg.server.utils.annotation.IntegrationTest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
@Transactional
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
    TierRepository tierRepository;

    @Autowired
    PChangeRepository pChangeRepository;
    @Autowired
    TournamentRepository tournamentRepository;
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
    private User user3;
    private Game game1;
    private Game game2;
    ArrayList<Tier> tiers;

    @BeforeEach
    void init() {
        tiers = testDataUtils.createTierSystem("pingpong");
        season = seasonRepository.save(new Season("test season", LocalDateTime.of(2023, 7, 14, 0, 0), LocalDateTime.of(2999, 12, 31, 23, 59),
                1000, 100));
        user1 = testDataUtils.createNewUser("test1", "test1@email", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
        accessToken = tokenProvider.createToken(user1.getId());
        user2 = testDataUtils.createNewUser("test2", "test2@email", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
        Tier tier = tiers.get(0);
        rankRepository.save(Rank.from(user1, season, season.getStartPpp(), tier));
        rankRepository.save(Rank.from(user2, season, season.getStartPpp(), tier));
        RankRedis userRank = RankRedis.from(UserDto.from(user1), season.getStartPpp(), tier.getImageUri());
        String redisHashKey = RedisKeyManager.getHashKey(season.getId());
        rankRedisRepository.addRankData(redisHashKey, user1.getId(), userRank);
        userRank = RankRedis.from(UserDto.from(user2), season.getStartPpp(), tier.getImageUri());
        rankRedisRepository.addRankData(redisHashKey, user2.getId(), userRank);

        game1 = gameRepository.save(new Game(season, StatusType.END, Mode.RANK, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
        Team team1 = teamRepository.save(new Team(game1, 1, false));
        Team team2 = teamRepository.save(new Team(game1, 2, true));
        teamUserRepository.save(new TeamUser(team1, user1));
        teamUserRepository.save(new TeamUser(team2, user2));
        game2 = gameRepository.save(new Game(season, StatusType.WAIT, Mode.RANK, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
        team1 = teamRepository.save(new Team(game2, 1, false));
        team2 = teamRepository.save(new Team(game2, 2, true));
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

    // GET /pingpong/games/{gameId}
    @Nested
    @DisplayName("게임 조회 테스트")
    class GetGameInfoTest {
        /**
         * getGameInfo() -> GameFindService.getGameInfo()
         */
        @Test
        @DisplayName("유저 쿼리 포함 성공")
        public void getGameInfoTest() throws Exception {
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
    }

    // GET /pingpong/games/normal
    @Nested
    @DisplayName("normal 게임 조회")
    class NormalGameListTest {
        /**
         * GET /pingpong/games/normal?page=1&size=10
         * normalGameList() -> GameFindService.normalGameList()
         */
        @Test
        @DisplayName("조회 성공")
        public void success() throws Exception {
            //given
            testDataUtils.createGame(user1, LocalDateTime.now().minusMinutes(16),
                LocalDateTime.now().minusMinutes(1), season, Mode.NORMAL);
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

        /**
         * GET /pingpong/games/normal?page=1&size=10&intraId=test1
         * normalGameList() -> GameFindService.normalGameListByIntra()
         */
        @Test
        @DisplayName("유저 쿼리 포함 성공")
        public void successUserQuery() throws Exception {
            //given
            String url = "/pingpong/games/normal?page=1&size=10&intraId=test1";
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
    }

    // GET /pingpong/games/rank
    @Nested
    @DisplayName("rank 게임 조회")
    class RankGameListTest {
        /**
         * GET /pingpong/games/rank?page=1&size=10&seasonId=1
         * rankGameList() -> GameFindService.rankGameList()
         */
        @Test
        @DisplayName("조회 성공")
        public void success() throws Exception {
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

        /**
         * GET /pingpong/games/rank?page=1&size=10&seasonId={seasonId}&nickname=test1
         * rankGameList() -> GameFindService.rankGameListByIntra()
         */
        @Test
        @DisplayName("nickname 쿼리 포함 조회 성공")
        public void successNicknameQuery() throws Exception {
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

        /**
         * GET /pingpong/games/rank?page=1&size=0
         * allGameList() -> GameFindService.allGameList()
         */
        @Test
        @DisplayName("Bad Request exception 발생")
        public void failBadRequest() throws Exception {
            //given
            String url = "/pingpong/games/rank?page=1&size=0";
            //then
            mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        }
    }

    // GET /pingpong/games
    @Nested
    @DisplayName("전체 게임 목록 조회")
    class AllGameListTest {
        /**
         * GET /pingpong/games?page=1&size=10
         */
        @Test
        @DisplayName("조회 성공")
        public void success() throws Exception {
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

        /**
         * GET /pingpong/games?page=1&size=10&nickname=test1
         */
        @Test
        @DisplayName("nickname 쿼리 포함 조회 성공")
        public void suceessNicknameQuery() throws Exception {
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

        /**
         * GET /pingpong/games?pageNum=1&pageSize=10&status=live
         */
        @Test
        @DisplayName("잘못된 query parameter인 경우, Bad Request exception 발생")
        public void failBadRequest() throws Exception {
            String url = "/pingpong/games?pageNum=1&pageSize=10&status=live";   // LIVE 대문자여야 함
            mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        }

        /**
         * GET /pingpong/games?page=1&size=10&status=2
         */
        @Test
        @DisplayName("잘못된 query parameter인 경우, Bad Request exception 발생")
        public void failBadRequest2() throws Exception {
            String url = "/pingpong/games?page=1&size=10&status=2";
            String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
            System.out.println(contentAsString);
        }
    }

    // POST /pingpong/games/rank
    @Nested
    @DisplayName("랭크 게임 점수 결과 입력")
    class CreateRankResultTest {
        /**
         * POST /pingpong/games/rank
         */
        @Test
        @DisplayName("둘 중 한명 입력 후 나머지 한 명이 입력할 경우 conflict exception 발생")
        public void success() throws Exception {
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

        // TODO : 랭크 게임 결과 입력 실패 테스트 (잘못된 점수 입력할 경우 InvalidParameterException 발생)
    }

    @Nested
    @DisplayName("토너먼트 게임 점수 결과 입력")
    class CreateTournamentResultTest {
        @Test
        @DisplayName("입력 성공")
        public void success() throws Exception {
            //given
            String url = "/pingpong/games/tournament";
            Tournament tournament = testDataUtils.createTournamentWithUser(Tournament.ALLOWED_JOINED_NUMBER, 4, "ttt");
            List<TournamentGame> tournamentGameList = testDataUtils.createTournamentGameList(tournament, Tournament.ALLOWED_JOINED_NUMBER - 1);
            // 8강 경기 생성
            Game game = null;
            Team team1 = null;
            Team team2 = null;
            for (int i = 0; i < Tournament.ALLOWED_JOINED_NUMBER / 2; ++i) {
                game = new Game(season, StatusType.WAIT, Mode.TOURNAMENT, LocalDateTime.now().minusMinutes(15), LocalDateTime.now());
                team1 = new Team(game, -1, false);
                team2 = new Team(game, -1, false);
                TeamUser teamUser1 = new TeamUser(team1, tournament.getTournamentUsers().get(i * 2).getUser());
                TeamUser teamUser2 = new TeamUser(team2, tournament.getTournamentUsers().get(i * 2 + 1).getUser());
                gameRepository.save(game);
                tournamentGameList.get(i).updateGame(game);
            }
            String ac1 = tokenProvider.createToken(team1.getTeamUsers().get(0).getUser().getId());
            String content = objectMapper.writeValueAsString(new TournamentResultReqDto(game.getId(), team1.getId(), 1, team2.getId(), 2));

            //when
            String contentAsString = mockMvc.perform(post(url)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            //then
            assertThat(game.getStatus()).isEqualTo(StatusType.END);
            System.out.println(contentAsString);
        }

        @Test
        @DisplayName("잘못된 Game Id")
        public void invalidGameId() throws Exception {
            //given
            String url = "/pingpong/games/tournament";
            Game game = gameRepository.save(new Game(season, StatusType.WAIT, Mode.TOURNAMENT, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
            Team team1 = teamRepository.save(new Team(game, -1, false));
            Team team2 = teamRepository.save(new Team(game, -1, false));
            String ac1 = tokenProvider.createToken(user1.getId());
            teamUserRepository.save(new TeamUser(team1, user1));
            teamUserRepository.save(new TeamUser(team2, user2));
            String content = objectMapper.writeValueAsString(new TournamentResultReqDto(99999999L, team1.getId(), 1, team2.getId(), 2));
            //when
            String contentAsString = mockMvc.perform(post(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();
            //then
            System.out.println(contentAsString);
        }

        @Test
        @DisplayName("잘못된 Team Id")
        public void invalidTeamId() throws Exception {
            //given
            String url = "/pingpong/games/tournament";
            Game game = gameRepository.save(new Game(season, StatusType.WAIT, Mode.TOURNAMENT, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
            Game game2 = gameRepository.save(new Game(season, StatusType.WAIT, Mode.TOURNAMENT, LocalDateTime.now().minusMinutes(30), LocalDateTime.now().minusMinutes(15)));
            Team team1 = teamRepository.save(new Team(game, -1, false));
            Team team2 = teamRepository.save(new Team(game, -1, false));
            Team team3 = teamRepository.save(new Team(game2, -1, false));
            String ac1 = tokenProvider.createToken(user1.getId());
            teamUserRepository.save(new TeamUser(team1, user1));
            teamUserRepository.save(new TeamUser(team2, user2));
            String content1 = objectMapper.writeValueAsString(new TournamentResultReqDto(game.getId(), -1L, 1, team2.getId(), 2));
            String content2 = objectMapper.writeValueAsString(new TournamentResultReqDto(game.getId(), team1.getId(), 1, -1L, 2));
            String content3 = objectMapper.writeValueAsString(new TournamentResultReqDto(game.getId(), team1.getId(), 1, team3.getId(), 2));
            //when1 - 존재하지 않는 myTeamId
            String contentAsString1 = mockMvc.perform(post(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content1))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
            //then
            System.out.println(contentAsString1);

            //when2 - 존재하지 않는 enemyTeamId
            String contentAsString2 = mockMvc.perform(post(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content2))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
            //then2
            System.out.println(contentAsString2);

            //when3 - game에 존재하지 않은 TeamId
            String contentAsString3 = mockMvc.perform(post(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content3))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();
            //then3
            System.out.println(contentAsString2);
        }

        @Test
        @DisplayName("잘못된 점수")
        public void invalidScore() throws Exception {
            //given
            String url = "/pingpong/games/tournament";
            Game game = gameRepository.save(new Game(season, StatusType.WAIT, Mode.TOURNAMENT, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
            Team team1 = teamRepository.save(new Team(game, -1, false));
            Team team2 = teamRepository.save(new Team(game, -1, false));
            String ac1 = tokenProvider.createToken(user1.getId());
            teamUserRepository.save(new TeamUser(team1, user1));
            teamUserRepository.save(new TeamUser(team2, user2));
            String content1 = objectMapper.writeValueAsString(new TournamentResultReqDto(game.getId(), team1.getId(), -1, team2.getId(), 2));
            String content2 = objectMapper.writeValueAsString(new TournamentResultReqDto(game.getId(), team1.getId(), 1, team2.getId(), 1));
            String content3 = objectMapper.writeValueAsString(new TournamentResultReqDto(game.getId(), team1.getId(), 3, team2.getId(), 0));
            //when1
            String contentAsString1 = mockMvc.perform(post(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content1))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
            //then1
            System.out.println(contentAsString1);

            //when2
            String contentAsString2 = mockMvc.perform(post(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content2))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
            //then2
            System.out.println(contentAsString2);

            //when3
            String contentAsString3 = mockMvc.perform(post(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content3))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
            //then3
            System.out.println(contentAsString3);
        }

        @Test
        @DisplayName("잘못된 Game Status")
        public void invalidStatus() throws Exception {
            //given
            String url = "/pingpong/games/tournament";
            Game game = gameRepository.save(new Game(season, StatusType.END, Mode.TOURNAMENT, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
            Team team1 = teamRepository.save(new Team(game, -1, false));
            Team team2 = teamRepository.save(new Team(game, -1, false));
            String ac1 = tokenProvider.createToken(user1.getId());
            teamUserRepository.save(new TeamUser(team1, user1));
            teamUserRepository.save(new TeamUser(team2, user2));
            String content = objectMapper.writeValueAsString(new TournamentResultReqDto(game.getId(), team1.getId(), 1, team2.getId(), 2));
            //when
            String contentAsString = mockMvc.perform(post(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
            //then
            System.out.println(contentAsString);
        }

        @Test
        @DisplayName("이미 점수 입력이 완료된 게임")
        public void scoreAlreadyEntered() throws Exception {
            //given
            String url = "/pingpong/games/tournament";
            Game game = gameRepository.save(new Game(season, StatusType.BEFORE, Mode.TOURNAMENT, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
            Team team1 = teamRepository.save(new Team(game, 2, true));
            Team team2 = teamRepository.save(new Team(game, 1, false));
            String ac1 = tokenProvider.createToken(user1.getId());
            teamUserRepository.save(new TeamUser(team1, user1));
            teamUserRepository.save(new TeamUser(team2, user2));
            String content = objectMapper.writeValueAsString(new TournamentResultReqDto(game.getId(), team1.getId(), 2, team2.getId(), 1));
            //when
            String contentAsString = mockMvc.perform(post(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
            //then
            System.out.println(contentAsString);
        }

        @Test
        @DisplayName("잘못된 Team User")
        public void invalidTeamUser() throws Exception {
            //given
            String url = "/pingpong/games/tournament";
            user3 = testDataUtils.createNewUser("test3", "test3@email", RacketType.NONE, SnsType.EMAIL, RoleType.USER);
            Game game = gameRepository.save(new Game(season, StatusType.WAIT, Mode.TOURNAMENT, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
            Team team1 = teamRepository.save(new Team(game, -1, false));
            Team team2 = teamRepository.save(new Team(game, -1, false));
            String ac1 = tokenProvider.createToken(user1.getId());
            teamUserRepository.save(new TeamUser(team1, user3));
            teamUserRepository.save(new TeamUser(team2, user2));
            String content = objectMapper.writeValueAsString(new TournamentResultReqDto(game.getId(), team1.getId(), 1, team2.getId(), 2));
            //when
            String contentAsString1 = mockMvc.perform(post(url)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ac1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
            //then
            System.out.println(contentAsString1);
        }
    }

    // POST /pingpong/games/normal
    @Nested
    @DisplayName("일반 게임 종료 버튼 클릭")
    class CreateNormalResultTest {
        /**
         * POST /pingpong/games/normal
         */
        @Test
        @DisplayName("게임 종료")
        public void success() throws Exception {
            String url = "/pingpong/games/normal";
            Game game = gameRepository.save(new Game(season, StatusType.WAIT, Mode.NORMAL, LocalDateTime.now().minusMinutes(15), LocalDateTime.now()));
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
                .andExpect(status().isCreated())
                .andReturn().getResponse();
            System.out.println(user1.getTotalExp());
            System.out.println(user2.getTotalExp());
        }
        // TODO HttpStatus.ACCEPTED 테스트 - service 함수에서 BEFORE 상태일 때 false 리턴할 경우
    }

    // GET /pingpong/games/{gameId}/result/rank
    @Nested
    @DisplayName("랭크 게임 결과 조회")
    class GetRankPPPChangeTest {
        /**
         * GET /pingpong/games/{gameId}/result/rank
         */
        @Test
        @DisplayName("랭크 게임 결과 조회")
        public void successGetRankPPPChange() throws Exception {
            Game mockMatch = testDataUtils.createMockMatch(user1, season,
                LocalDateTime.now().minusMinutes(16),
                LocalDateTime.now().minusMinutes(1), Mode.RANK);
            testDataUtils.createUserRank(user1, "hello", season, tiers.get(0));
            testDataUtils.createCoinPolicy(user1, 0, 0, 1, 0);

            String url = "/pingpong/games/" + mockMatch.getId() + "/result/rank";
            String content = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
            System.out.println("result: " + content);
        }
    }

    // GET /pingpong/games/{gameId}/result/normal
    @Nested
    @DisplayName("일반 게임 결과 조회")
    class GetNormalExpChangeTest {
        /**
         * GET /pingpong/games/{gameId}/result/normal
         */
        @Test
        @DisplayName("일반 게임 결과 조회")
        public void normalGameResult() throws Exception {
            Game mockMatch = testDataUtils.createMockMatch(user1, season,
                LocalDateTime.now().minusMinutes(16),
                LocalDateTime.now().minusMinutes(1), Mode.NORMAL);
            testDataUtils.createCoinPolicy(user1, 0, 1, 0, 0);
            String url = "/pingpong/games/" + mockMatch.getId() + "/result/normal";
            String content = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
            System.out.println("result: " + content);
        }
    }
}
