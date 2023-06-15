package com.gg.server.admin.game.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gg.server.admin.game.dto.GameLogListAdminResponseDto;
import com.gg.server.admin.game.dto.RankGamePPPModifyReqDto;
import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.dto.req.RankResultReqDto;
import com.gg.server.domain.game.service.GameService;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.match.dto.GameAddDto;
import com.gg.server.domain.match.service.GameUpdateService;
import com.gg.server.domain.rank.data.Rank;
import com.gg.server.domain.rank.data.RankRepository;
import com.gg.server.domain.rank.redis.RankRedis;
import com.gg.server.domain.rank.redis.RankRedisRepository;
import com.gg.server.domain.rank.redis.RedisKeyManager;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.user.User;
import com.gg.server.domain.user.UserRepository;
import com.gg.server.domain.user.controller.dto.GameInfoDto;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.DoubleStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.completableFuture;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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


    @Test
    @DisplayName("[Get]/pingpong/admin/games/users?intraId=${intraId}&page=${pageNumber}&size={sizeNum}")
    void getUserGameList() throws Exception {
        String accessToken = testDataUtils.getAdminLoginAccessToken();
        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

        Integer currentPage = 2;
        Integer pageSize = 5;//페이지 사이즈 크기가 실제 디비 정보보다 큰지 확인할 것

        String url = "/pingpong/admin/games/users?intraId="
         + "nheo" + "&page=" + currentPage + "&size=" + pageSize;

        String contentAsString = mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameLogListAdminResponseDto result = objectMapper.readValue(contentAsString, GameLogListAdminResponseDto.class);
        assertThat(result.getGameLogList().size()).isEqualTo(pageSize);
        System.out.println(result.getGameLogList().get(0).getGameId());
        System.out.println(result.getGameLogList().get(0).getStartAt());
        System.out.println(result.getGameLogList().get(0).getMode());

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
        GameInfoDto game1Info = testDataUtils.createGame(adminUser, LocalDateTime.now().minusMinutes(5), LocalDateTime.now().plusMinutes(5), season, currentMatchMode);

        User enemyUser1 = userRepository.findById(game1Info.getEnemyUserId()).get();
        testDataUtils.createUserRank(adminUser, "adminUserMessage", season);
        testDataUtils.createUserRank(enemyUser1, "enemy111UserMessage", season);

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
        Thread.sleep(1000);
        //////////////////////////////
        GameInfoDto game2Info = testDataUtils.createGame(adminUser, LocalDateTime.now().minusMinutes(4), LocalDateTime.now().plusMinutes(6), season, currentMatchMode);
        User enemyUser2 = userRepository.findById(game2Info.getEnemyUserId()).get();
        testDataUtils.createUserRank(adminUser, "adminUserMessage", season);
        testDataUtils.createUserRank(enemyUser2, "enemy222UserMessage", season);

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