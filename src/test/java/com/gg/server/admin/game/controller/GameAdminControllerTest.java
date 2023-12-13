package com.gg.server.admin.game.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.game.dto.GameLogListAdminResponseDto;
import com.gg.server.admin.game.dto.RankGamePPPModifyReqDto;
import com.gg.server.utils.annotation.IntegrationTest;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.dto.request.RankResultReqDto;
import com.gg.server.domain.game.service.GameService;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.match.service.GameUpdateService;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.tier.data.Tier;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.data.UserRepository;
import com.gg.server.domain.user.controller.dto.GameInfoDto;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("[Admin] Game Admin Controller Integration Test")
class GameAdminControllerTest {
    @Autowired
    TestDataUtils testDataUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthTokenProvider tokenProvider;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GameUpdateService gameUpdateService;

    @Autowired
    GameService gameService;

    @Autowired
    RankRepository rankRepository;

    @Autowired
    RankRedisRepository rankRedisRepository;

    @AfterEach
    void redisDown() {
        rankRedisRepository.deleteAll();
    }

    @Nested
    @DisplayName("[GET] /pingpong/admin/games/users?intraId=${intraId}&page=${pageNumber}&size={sizeNum}")
    class GetUserGameList {
        String accessToken;
        Long userId;
        User user;
        Season season;

        static final int TOTAL_PAGE_SIZE = 18;

        static final String INTRA_ID = "nheo";

        @BeforeEach
        void setUp() {
            accessToken = testDataUtils.getAdminLoginAccessToken();
            userId = tokenProvider.getUserIdFromAccessToken(accessToken);
            user = testDataUtils.createNewUser(INTRA_ID);
            season = testDataUtils.createSeason();
            testDataUtils.createUserRank(user, "status message", season);
            for (int i = 0; i < TOTAL_PAGE_SIZE; i++) {
                testDataUtils.createMockMatchWithMockRank(user, season, LocalDateTime.now().minusMinutes(20 + i * 15), LocalDateTime.now().minusMinutes(5 + i * 15));
            }
        }
        private GameLogListAdminResponseDto getPageResult(int currentPage, int pageSize)
            throws Exception {
            String url = "/pingpong/admin/games/users?intraId="
                + INTRA_ID + "&page=" + currentPage + "&size=" + pageSize;

            String contentAsString = mockMvc
                .perform(get(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            return objectMapper.readValue(contentAsString, GameLogListAdminResponseDto.class);
        }
        @Test
        @Transactional
        @DisplayName("First page")
        void getUserGameListFirstPage() throws Exception {
            //given
            int pageSize = 5;
            //when
            GameLogListAdminResponseDto result = getPageResult(1, 5);
            //then
            assertThat(result.getGameLogList().size()).isEqualTo(pageSize);
        }

        @Test
        @Transactional
        @DisplayName("Middle page")
        void getUserGameListMidPage() throws Exception {
            //given
            int pageSize = 5;
            //when
            GameLogListAdminResponseDto result = getPageResult(2, 5);
            //then
            assertThat(result.getGameLogList().size()).isEqualTo(pageSize);
        }

        @Test
        @Transactional
        @DisplayName("End page")
        void getUserGameListEndPage() throws Exception {
            //given
            int pageSize = 5;
            //when
            GameLogListAdminResponseDto result = getPageResult(4, 5);
            //then
            assertThat(result.getGameLogList().size()).isEqualTo(TOTAL_PAGE_SIZE % pageSize);
        }
    }

    @Test
    @DisplayName("[PUT] /pingpong/admin/games")
    @Transactional
    public void 관리자게임전적수정테스트() throws Exception {
        String url = "/pingpong/admin/games";
        Mode currentMatchMode = Mode.RANK;
        Season season = testDataUtils.createSeason();
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        Long adminUserId = tokenProvider.getUserIdFromAccessToken(accessToken);
        User adminUser = userRepository.findById(adminUserId).get();
        ArrayList<Tier> tierList = testDataUtils.createTierSystem("pinpong");
        GameInfoDto game1Info = testDataUtils.createGameWithTierAndRank(adminUser, LocalDateTime.now().minusMinutes(5), LocalDateTime.now().plusMinutes(5), season, currentMatchMode, tierList.get(0));

        User enemyUser1 = userRepository.findById(game1Info.getEnemyUserId()).get();

        RankResultReqDto rankResultReqDto = new RankResultReqDto(game1Info.getGameId(),
                game1Info.getMyTeamId(),
                2,
                game1Info.getEnemyTeamId(),
                1);
        gameService.createRankResult(rankResultReqDto, adminUserId);

        Rank adminUserRank = rankRepository.findByUserIdAndSeasonId(adminUserId, season.getId()).get();
        Rank enemyUser1Rank = rankRepository.findByUserIdAndSeasonId(enemyUser1.getId(), season.getId()).get();
        System.out.println("MANGO ADMIN1 before DB PPP : " + adminUserRank.getPpp());
        System.out.println("MANGO ENEMY1 before DB PPP : " + enemyUser1Rank.getPpp());


        RankGamePPPModifyReqDto modifyReqDto = new RankGamePPPModifyReqDto(game1Info.getMyTeamId(), 1, game1Info.getEnemyTeamId(), 0);
        mockMvc.perform(put("/pingpong/admin/games/" + game1Info.getGameId())
                        .content(objectMapper.writeValueAsString(modifyReqDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        GameTeamUser historyGame1 = gameRepository.findTeamsByGameIsIn(List.of(game1Info.getGameId())).get(0);

        adminUserRank = rankRepository.findByUserIdAndSeasonId(adminUserId, season.getId()).get();
        enemyUser1Rank = rankRepository.findByUserIdAndSeasonId(enemyUser1.getId(), season.getId()).get();
        System.out.println("MANGO ADMIN1 after DB PPP : " + adminUserRank.getPpp());
        System.out.println("MANGO ENEMY1 after DB PPP : " + enemyUser1Rank.getPpp());

        //////////////////////////////
        sleep(1000);
        //////////////////////////////
        GameInfoDto game2Info = testDataUtils.createGameWithTierAndRank(adminUser, LocalDateTime.now().minusMinutes(4), LocalDateTime.now().plusMinutes(6), season, currentMatchMode, tierList.get(0));
        User enemyUser2 = userRepository.findById(game2Info.getEnemyUserId()).get();

        rankResultReqDto = new RankResultReqDto(game2Info.getGameId(),
                game2Info.getMyTeamId(),
                1,
                game2Info.getEnemyTeamId(),
                2);
        gameService.createRankResult(rankResultReqDto, adminUserId);

        adminUserRank = rankRepository.findByUserIdAndSeasonId(adminUserId, season.getId()).get();
        Rank enemyUser2Rank = rankRepository.findByUserIdAndSeasonId(enemyUser2.getId(), season.getId()).get();
        System.out.println("MANGO ADMIN2 before DB PPP : " + adminUserRank.getPpp());
        System.out.println("MANGO ENEMY2 before DB PPP : " + enemyUser2Rank.getPpp());


        modifyReqDto = new RankGamePPPModifyReqDto(game2Info.getMyTeamId(), 0, game2Info.getEnemyTeamId(), 1);
        mockMvc.perform(put("/pingpong/admin/games/" + game2Info.getGameId())
                        .content(objectMapper.writeValueAsString(modifyReqDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        adminUserRank = rankRepository.findByUserIdAndSeasonId(adminUserId, season.getId()).get();
        enemyUser2Rank = rankRepository.findByUserIdAndSeasonId(enemyUser2.getId(), season.getId()).get();
        System.out.println("MANGO ADMIN2 after DB PPP : " + adminUserRank.getPpp());
        System.out.println("MANGO ENEMY2 after DB PPP : " + enemyUser2Rank.getPpp());
    }
}