package com.gg.server.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.domain.coin.data.CoinHistoryRepository;
import com.gg.server.domain.coin.data.CoinPolicyRepository;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.req.RankResultReqDto;
import com.gg.server.domain.game.service.GameService;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.season.data.SeasonRepository;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.controller.dto.GameInfoDto;
import com.gg.server.domain.user.dto.*;
import com.gg.server.domain.user.type.EdgeType;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class UserControllerTest {

    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RankRedisRepository redisRepository;

    @Autowired
    RankRepository rankRepository;

    @Autowired
    SeasonRepository seasonRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GameService gameService;

    @Autowired
    CoinPolicyRepository coinPolicyRepository;

    @Autowired
    CoinHistoryRepository coinHistoryRepository;


    @AfterEach
    public void flushRedis() {
        redisRepository.deleteAll();
    }

    @Test
    @DisplayName("live")
    public void userLiveTest() throws Exception {
        String accessToken = testDataUtils.getLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        String liveUrl = "/pingpong/users/live";
        String event = "game";
        int notiCnt = 2;
        Mode currentMatchMode = Mode.RANK;
        GameInfoDto gameInfo = testDataUtils.addMockDataUserLiveApi(event, notiCnt, currentMatchMode.getCode(), userId);
        Game testGame = gameRepository.getById(gameInfo.getGameId());

        // Rank Live 게임 테스트
        String contentAsString1 = mockMvc.perform(get(liveUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserLiveResponseDto userLiveResponseDto1 = objectMapper.readValue(contentAsString1, UserLiveResponseDto.class);
        assertThat(userLiveResponseDto1.getEvent()).isEqualTo(event);
        assertThat(userLiveResponseDto1.getNotiCount()).isEqualTo(notiCnt);
        assertThat(userLiveResponseDto1.getCurrentMatchMode()).isEqualTo(currentMatchMode);
        assertThat(userLiveResponseDto1.getGameId()).isEqualTo(gameInfo.getGameId());

        // Rank 점수 입력 테스트
        RankResultReqDto rankResultReqDto = new RankResultReqDto(gameInfo.getGameId(),
                gameInfo.getEnemyTeamId(), 1,
                gameInfo.getMyTeamId(), 2);
        assertThat(testGame.getStatus()).isEqualTo(StatusType.LIVE);
        gameService.createRankResult(rankResultReqDto, gameInfo.getEnemyUserId());
        assertThat(testGame.getStatus()).isEqualTo(StatusType.END);

        String contentAsString2 = mockMvc.perform(get(liveUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserLiveResponseDto userLiveResponseDto2 = objectMapper.readValue(contentAsString2, UserLiveResponseDto.class);
        assertThat(userLiveResponseDto2.getEvent()).isEqualTo(event);
        assertThat(userLiveResponseDto2.getNotiCount()).isEqualTo(notiCnt);
        assertThat(userLiveResponseDto2.getCurrentMatchMode()).isEqualTo(currentMatchMode);
        assertThat(userLiveResponseDto2.getGameId()).isEqualTo(gameInfo.getGameId());

        // Rank PChange is_checked 테스트
        String contentAsString3 = mockMvc.perform(get(liveUrl).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserLiveResponseDto userLiveResponseDto3 = objectMapper.readValue(contentAsString3, UserLiveResponseDto.class);
        assertThat(userLiveResponseDto3.getEvent()).isEqualTo(null);
        assertThat(userLiveResponseDto3.getNotiCount()).isEqualTo(notiCnt);
        assertThat(userLiveResponseDto3.getCurrentMatchMode()).isEqualTo(null);
        assertThat(userLiveResponseDto3.getGameId()).isEqualTo(null);
    }

    @Test
    @DisplayName("/")
    public void userNormalDetail() throws Exception {
        //given
        String url = "/pingpong/users";
        String intraId = "intra";
        String email = "email";
        String imageUrl = "imageUrl";
        User newUser = testDataUtils.createNewUser(intraId, email, imageUrl, RacketType.PENHOLDER,
                SnsType.BOTH, RoleType.ADMIN);
        String accessToken = tokenProvider.createToken(newUser.getId());

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserNormalDetailResponseDto responseDto = objectMapper.readValue(contentAsString, UserNormalDetailResponseDto.class);

        //then
        assertThat(responseDto.getIntraId()).isEqualTo(intraId);
        assertThat(responseDto.getUserImageUri()).isEqualTo(imageUrl);
        assertThat(responseDto.getIsAdmin()).isTrue();
        assertThat(responseDto.getIsAttended());
    }

    @Test
    @DisplayName("searches?intraId=${IntraId}")
    public void searchUser() throws Exception {
        //given
        String intraId[] = {"intraId", "2intra2", "2intra", "aaaa", "bbbb"};
        String email = "email";
        String imageUrl = "imageUrl";
        User user = null;
        for (String intra : intraId) {
            user = testDataUtils.createNewUser(intra, email, imageUrl, RacketType.PENHOLDER,
                    SnsType.BOTH, RoleType.ADMIN);
        }
        String accessToken = tokenProvider.createToken(user.getId());
        String keyWord = "intra";
        String url = "/pingpong/users/searches?intraId=" + keyWord;

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserSearchResponseDto userSearchResponseDto = objectMapper.readValue(contentAsString, UserSearchResponseDto.class);

        //then
        assertThat(userSearchResponseDto.getUsers().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("[GET] {targetId}")
    public void getUserDetail() throws Exception {
        //given
        Season season = testDataUtils.createSeason();
        String intraId = "intraId";
        String email = "email";
        String imageUrl = "imageUrl";
        String statusMessage = "statusMessage";
        User newUser = testDataUtils.createNewUser(intraId, email, imageUrl, RacketType.PENHOLDER,
                SnsType.BOTH, RoleType.ADMIN);
        String accessToken = tokenProvider.createToken(newUser.getId());
        testDataUtils.createUserRank(newUser, statusMessage, season);
        String url = "/pingpong/users/" + newUser.getIntraId();

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        UserDetailResponseDto responseDto = objectMapper.readValue(contentAsString, UserDetailResponseDto.class);

        //then
        Assertions.assertThat(responseDto.getIntraId()).isEqualTo(intraId);
        Assertions.assertThat(responseDto.getUserImageUri()).isEqualTo(imageUrl);
        Assertions.assertThat(responseDto.getStatusMessage()).isEqualTo(statusMessage);
        Assertions.assertThat(responseDto.getLevel()).isEqualTo(1);
        Assertions.assertThat(responseDto.getCurrentExp()).isEqualTo(0);
        System.out.println(responseDto);
    }

    @Test
    @DisplayName("/{intraId}/rank?season={seasonId}")
    public void userRankDetail() throws Exception {
        //given
        Season season = testDataUtils.createSeason();
        User newUser = testDataUtils.createNewUser();
        String accessToken = tokenProvider.createToken(newUser.getId());
        testDataUtils.createUserRank(newUser, "statusMessage", season);

        //when
        String url = "/pingpong/users/" + newUser.getIntraId() + "/rank?season=" + season.getId();
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        UserRankResponseDto responseDto = objectMapper.readValue(contentAsString, UserRankResponseDto.class);

        //then
        Assertions.assertThat(responseDto.getRank()).isEqualTo(-1);
        Assertions.assertThat(responseDto.getWins()).isEqualTo(0);
        Assertions.assertThat(responseDto.getLosses()).isEqualTo(0);
        Assertions.assertThat(responseDto.getPpp()).isEqualTo(season.getStartPpp());
        System.out.println(responseDto);
    }

    @Test
    @DisplayName("/{intraId}/historics?season={seasonId}")
    public void getUserHistory() throws Exception {
        //given
        Season season = testDataUtils.createSeason();
        User newUser = testDataUtils.createNewUser();
        String accessToken = tokenProvider.createToken(newUser.getId());

        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = startTime.plusMinutes(15);
        testDataUtils.createMockMatch(newUser, season, startTime, endTime);

        LocalDateTime startTime1 = LocalDateTime.now().minusDays(2);
        LocalDateTime endTime1 = startTime1.plusMinutes(15);
        testDataUtils.createMockMatch(newUser, season, startTime1, endTime1);

        LocalDateTime startTime2 = LocalDateTime.now().minusDays(3);
        LocalDateTime endTime2 = startTime2.plusMinutes(15);
        testDataUtils.createMockMatch(newUser, season, startTime2, endTime2);

        String url = "/pingpong/users/" + newUser.getIntraId() + "/historics?season=" + season.getId();

        //when
        String contentAsString = mockMvc.perform(get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        UserHistoryResponseDto responseDto = objectMapper.readValue(contentAsString, UserHistoryResponseDto.class);


        //then
        List<UserHistoryData> historics = responseDto.getHistorics();
        Assertions.assertThat(responseDto.getHistorics().size()).isEqualTo(3);
        Assertions.assertThat(historics)
                .isSortedAccordingTo(Comparator.comparing(UserHistoryData::getDate));

    }

    @Test
    @DisplayName("[put] {intraId}")
    public void updateUser() throws Exception {
        //given
        Season season = testDataUtils.createSeason();
        String intraId = "intraId";
        String email = "email";
        String imageUrl = "imageUrl";
        User newUser = testDataUtils.createNewUser(intraId, email, imageUrl, RacketType.PENHOLDER,
                SnsType.BOTH, RoleType.ADMIN);
        String statusMessage = "statusMessage";
        testDataUtils.createUserRank(newUser, statusMessage, season);
        String accessToken = tokenProvider.createToken(newUser.getId());
        String url = "/pingpong/users/" + newUser.getIntraId();

        String newStatusMessage = "newStatusMessage";
        RacketType newRacketType = RacketType.SHAKEHAND;
        SnsType newSnsType = SnsType.SLACK;

        //when
        mockMvc.perform(put(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserModifyRequestDto(newRacketType, newStatusMessage, newSnsType))))
                .andExpect(status().isOk());
        //then
        String hashKey = RedisKeyManager.getHashKey(season.getId());
        RankRedis rank = redisRepository.findRankByUserId(hashKey, newUser.getId());
        rankRepository.findByUserIdAndSeasonId(newUser.getId(), season.getId()).ifPresentOrElse(rank1 -> {
            Assertions.assertThat(rank1.getStatusMessage()).isEqualTo(newStatusMessage);
        }, () -> {
            Assertions.fail("랭크 업데이트 실패");
        });
        userRepository.findById(newUser.getId()).ifPresentOrElse(user -> {
            Assertions.assertThat(user.getRacketType()).isEqualTo((newRacketType));
            Assertions.assertThat(user.getSnsNotiOpt()).isEqualTo(newSnsType);
            Assertions.assertThat(rank.getStatusMessage()).isEqualTo(newStatusMessage);
        }, () -> {
            Assertions.fail("유저 업데이트 실패");
        });
    }

    @Test
    @DisplayName("[post] /attendance")
    public void attendUserTest() throws Exception {
        //given
        String accessToken = testDataUtils.getLoginAccessToken();
        String url = "/pingpong/users/attendance";

        //when
        String contentAsString = mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UserAttendanceResponseDto result = objectMapper.readValue(contentAsString, UserAttendanceResponseDto.class);

        //then
        System.out.println(result.getAfterCoin());
        Assertions.assertThat(result.getAfterCoin() - result.getBeforeCoin()).isEqualTo(result.getCoinIncrement());
    }
  
    @Test
    @DisplayName("[patch] text-color")
    public void updateTextColorTest() throws Exception {
        //given
        Season season = testDataUtils.createSeason();
        String intraId = "intraId";
        String email = "email";
        String imageUrl = "imageUrl";
        User newUser = testDataUtils.createNewUser(intraId, email, imageUrl, RacketType.PENHOLDER,
                SnsType.BOTH, RoleType.ADMIN);
        String statusMessage = "statusMessage";
        testDataUtils.createUserRank(newUser, statusMessage, season);
        String accessToken = tokenProvider.createToken(newUser.getId());
        String url = "/pingpong/users/text-color";

        String newTextColor = "#FFFFFF";

        //when
        mockMvc.perform(patch(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserTextColorDto(newTextColor))))
                .andExpect(status().is2xxSuccessful());
        //then
        userRepository.findById(newUser.getId()).ifPresentOrElse(user -> {
            Assertions.assertThat(user.getTextColor()).isEqualTo(newTextColor);
        }, () -> {
            Assertions.fail("유저 업데이트 실패");
        });
    }

    @Test
    @DisplayName("[patch] edge")
    public void updateEdgeTest() throws Exception {
        //given
        Season season = testDataUtils.createSeason();
        String intraId = "intraId";
        String email = "email";
        String imageUrl = "imageUrl";
        User newUser = testDataUtils.createNewUser(intraId, email, imageUrl, RacketType.PENHOLDER,
                SnsType.BOTH, RoleType.ADMIN);
        String statusMessage = "statusMessage";
        testDataUtils.createUserRank(newUser, statusMessage, season);
        String accessToken = tokenProvider.createToken(newUser.getId());
        String url = "/pingpong/users/edge";

        EdgeType newEdge = EdgeType.BASIC;

        //when
        mockMvc.perform(patch(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserEdgeDto(newEdge))))
                .andExpect(status().is2xxSuccessful());
        //then
        log.info("newEdge : {}", newEdge);
        log.info("user.getEdge() : {}", newUser.getEdge());
        userRepository.findById(newUser.getId()).ifPresentOrElse(user -> {
            Assertions.assertThat(user.getEdge()).isEqualTo(newEdge);
        }, () -> {
            Assertions.fail("유저 업데이트 실패");
        });
    }
}