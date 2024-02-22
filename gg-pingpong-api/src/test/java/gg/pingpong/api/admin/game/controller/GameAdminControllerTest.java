package gg.pingpong.api.admin.game.controller;

import static java.lang.Thread.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.pingpong.api.admin.game.controller.response.GameLogListAdminResponseDto;
import gg.pingpong.api.admin.game.dto.RankGamePPPModifyReqDto;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.api.user.game.dto.request.RankResultReqDto;
import gg.pingpong.api.user.game.service.GameService;
import gg.pingpong.api.user.match.service.GameUpdateService;
import gg.pingpong.data.game.Rank;
import gg.pingpong.data.game.Season;
import gg.pingpong.data.game.Tier;
import gg.pingpong.data.game.type.Mode;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.game.GameRepository;
import gg.pingpong.repo.game.GameTeamUser;
import gg.pingpong.repo.rank.RankRepository;
import gg.pingpong.repo.rank.redis.RankRedisRepository;
import gg.pingpong.repo.user.UserRepository;
import gg.pingpong.utils.TestDataUtils;
import gg.pingpong.utils.annotation.IntegrationTest;
import gg.pingpong.utils.dto.GameInfoDto;
import lombok.RequiredArgsConstructor;

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
	@Autowired
	EntityManager entityManager;

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
		static final int TOURNAMENT_GAME_SIZE = 4;
		static final String INTRA_ID = "nheo";

		@BeforeEach
		void setUp() {
			accessToken = testDataUtils.getAdminLoginAccessToken();
			userId = tokenProvider.getUserIdFromAccessToken(accessToken);
			user = testDataUtils.createNewUser(INTRA_ID);
			season = testDataUtils.createSeason();
			testDataUtils.createUserRank(user, "status message", season);
			for (int i = 0; i < TOTAL_PAGE_SIZE; i++) {
				testDataUtils.createMockMatchWithMockRank(user, season, LocalDateTime.now().minusMinutes(20 + i * 15),
					LocalDateTime.now().minusMinutes(5 + i * 15));
			}
			for (int i = TOTAL_PAGE_SIZE; i < TOTAL_PAGE_SIZE + TOURNAMENT_GAME_SIZE; i++) {
				testDataUtils.createMockMatch(testDataUtils.createNewUser("testUser" + i), season,
					LocalDateTime.now().minusMinutes(20 + i * 15), LocalDateTime.now().minusMinutes(5 + i * 15),
					Mode.TOURNAMENT);
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
			GameLogListAdminResponseDto result = getPageResult(1, pageSize);
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
			GameLogListAdminResponseDto result = getPageResult(2, pageSize);
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
			GameLogListAdminResponseDto result = getPageResult(4, pageSize);
			//then
			assertThat(result.getGameLogList().size()).isEqualTo(TOTAL_PAGE_SIZE % pageSize);
		}
	}

	@Test
	@DisplayName("[PUT] /pingpong/admin/games 관리자게임전적수정테스트")
	@Transactional
	public void admingamestatUpdate() throws Exception {
		String url = "/pingpong/admin/games";
		Mode currentMatchMode = Mode.RANK;
		Season season = testDataUtils.createSeason();
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long adminUserId = tokenProvider.getUserIdFromAccessToken(accessToken);
		User adminUser = userRepository.findById(adminUserId).get();
		ArrayList<Tier> tierList = testDataUtils.createTierSystem("pinpong");
		GameInfoDto game1Info = testDataUtils.createGameWithTierAndRank(adminUser, LocalDateTime.now().minusMinutes(5),
			LocalDateTime.now().plusMinutes(5), season, currentMatchMode, tierList.get(0));

		User enemyUser1 = userRepository.findById(game1Info.getEnemyUserId()).get();

		RankResultReqDto rankResultReqDto = new RankResultReqDto(game1Info.getGameId(),
			game1Info.getMyTeamId(),
			2,
			game1Info.getEnemyTeamId(),
			1);
		gameService.createRankResult(rankResultReqDto, adminUserId);

		Rank adminUserRank = rankRepository.findByUserIdAndSeasonId(adminUserId, season.getId()).get();
		Rank enemyUser1Rank = rankRepository.findByUserIdAndSeasonId(enemyUser1.getId(),
			season.getId()).get();
		// win 1, losses 0, ppp 1020
		//        System.out.println("MANGO ADMIN1 before DB PPP : " + adminUserRank.getPpp());
		assertThat(adminUserRank.getLosses()).isEqualTo(0);
		assertThat(adminUserRank.getWins()).isEqualTo(1);
		// win 0, losses 1, ppp 982
		//        System.out.println("MANGO ENEMY1 before DB PPP : " + enemyUser1Rank.getPpp());
		assertThat(enemyUser1Rank.getWins()).isEqualTo(0);
		assertThat(enemyUser1Rank.getLosses()).isEqualTo(1);

		RankGamePPPModifyReqDto modifyReqDto = new RankGamePPPModifyReqDto(game1Info.getMyTeamId(), 1,
			game1Info.getEnemyTeamId(), 0);
		mockMvc.perform(put("/pingpong/admin/games/" + game1Info.getGameId())
				.content(objectMapper.writeValueAsString(modifyReqDto))
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse().getContentAsString();
		GameTeamUser historyGame1 = gameRepository.findTeamsByGameIsIn(List.of(game1Info.getGameId())).get(0);

		entityManager.flush();
		entityManager.clear();
		adminUserRank = rankRepository.findByUserIdAndSeasonId(adminUserId, season.getId()).get();
		enemyUser1Rank = rankRepository.findByUserIdAndSeasonId(enemyUser1.getId(), season.getId())
			.get();
		// win 1, losses 0, ppp 1020
		// MANGO ADMIN1 after DB
		assertThat(adminUserRank.getPpp()).isEqualTo(1020);
		assertThat(adminUserRank.getWins()).isEqualTo(1);
		assertThat(adminUserRank.getLosses()).isEqualTo(0);
		// win 0, losses 1, ppp 982
		// MANGO ENEMY1 after DB
		assertThat(enemyUser1Rank.getWins()).isEqualTo(0);
		assertThat(enemyUser1Rank.getLosses()).isEqualTo(1);
		assertThat(enemyUser1Rank.getPpp()).isEqualTo(982);
		//////////////////////////////
		sleep(1000);
		//////////////////////////////
		GameInfoDto game2Info = testDataUtils.createGameWithTierAndRank(adminUser, LocalDateTime.now().minusMinutes(4),
			LocalDateTime.now().plusMinutes(6), season, currentMatchMode, tierList.get(0));
		User enemyUser2 = userRepository.findById(game2Info.getEnemyUserId()).get();

		rankResultReqDto = new RankResultReqDto(game2Info.getGameId(),
			game2Info.getMyTeamId(),
			1,
			game2Info.getEnemyTeamId(),
			2);
		gameService.createRankResult(rankResultReqDto, adminUserId);

		adminUserRank = rankRepository.findByUserIdAndSeasonId(adminUserId, season.getId()).get();
		Rank enemyUser2Rank = rankRepository.findByUserIdAndSeasonId(enemyUser2.getId(),
			season.getId()).get();
		// win 1, losses 1, ppp 1001
		// MANGO ADMIN2 before DB
		assertThat(adminUserRank.getWins()).isEqualTo(1);
		assertThat(adminUserRank.getLosses()).isEqualTo(1);
		assertThat(adminUserRank.getPpp()).isEqualTo(1001);
		// win1, losses 0, ppp 1021
		// MANGO ENEMY2 before DB
		assertThat(enemyUser2Rank.getWins()).isEqualTo(1);
		assertThat(enemyUser2Rank.getLosses()).isEqualTo(0);
		assertThat(enemyUser2Rank.getPpp()).isEqualTo(1021);

		modifyReqDto = new RankGamePPPModifyReqDto(game2Info.getMyTeamId(), 2, game2Info.getEnemyTeamId(), 1);
		mockMvc.perform(put("/pingpong/admin/games/" + game2Info.getGameId())
				.content(objectMapper.writeValueAsString(modifyReqDto))
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().is2xxSuccessful())
			.andReturn().getResponse().getContentAsString();

		adminUserRank = rankRepository.findByUserIdAndSeasonId(adminUserId, season.getId()).get();
		enemyUser2Rank = rankRepository.findByUserIdAndSeasonId(enemyUser2.getId(), season.getId()).get();
		// win 2, losses 0, ppp 1038
		//        System.out.println("MANGO ADMIN2 after DB PPP : " + adminUserRank.getPpp() + ", WIN: "
		//                + adminUserRank.getWins() + ", LOSSES" + adminUserRank.getLosses());
		assertThat(adminUserRank.getWins()).isEqualTo(2);
		assertThat(adminUserRank.getLosses()).isEqualTo(0);
		assertThat(adminUserRank.getPpp()).isEqualTo(1038);
		// win 0, losses 1, ppp
		System.out.println("MANGO ENEMY2 after DB PPP : " + enemyUser2Rank.getPpp() + ", WIN: "
			+ enemyUser2Rank.getWins() + ", LOSSES" + enemyUser2Rank.getLosses());
		assertThat(enemyUser2Rank.getWins()).isEqualTo(0);
		assertThat(enemyUser2Rank.getLosses()).isEqualTo(1);
		//        assertThat(enemyUser2Rank.getPpp()).isEqualTo()
	}
}
