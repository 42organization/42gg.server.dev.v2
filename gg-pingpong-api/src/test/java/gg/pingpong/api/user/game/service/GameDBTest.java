package gg.pingpong.api.user.game.service;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.http.HttpHeaders;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import gg.data.game.Game;
import gg.data.game.Team;
import gg.data.game.TeamUser;
import gg.data.game.type.Mode;
import gg.data.season.Season;
import gg.data.user.User;
import gg.pingpong.api.admin.game.service.GameAdminService;
import gg.auth.utils.AuthTokenProvider;
import gg.repo.game.GameRepository;
import gg.repo.game.PChangeRepository;
import gg.repo.game.TeamRepository;
import gg.repo.game.TeamUserRepository;
import gg.repo.rank.redis.RankRedisRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@IntegrationTest
@RequiredArgsConstructor
@AutoConfigureMockMvc
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
	GameAdminService gameAdminService;
	@Autowired
	TeamRepository teamRepository;
	@Autowired
	TeamUserRepository teamUserRepository;
	@Autowired
	PChangeRepository pChangeRepository;
	@Autowired
	EntityManager em;
	@Autowired
	MockMvc mockMvc;

	@Test
	@DisplayName(value = "Cascade 종속삭제테스트")
	@Transactional
	public void cascadeTest() throws Exception {
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

	@Test
	@DisplayName(value = "game 전적조회 쿼리 수 테스트")
	@Transactional
	public void gameStatusQuery() throws Exception {
		Season season = testDataUtils.createSeason();
		User user = testDataUtils.createNewUser();
		for (int i = 0; i < 20; i++) {
			testDataUtils.createMockMatch(user, season, LocalDateTime.now().minusMinutes(15 * i + 20),
				LocalDateTime.now().minusMinutes(15 * i + 5), Mode.RANK, 2, 1);
		}
		//given
		String accessToken = testDataUtils.getAdminLoginAccessToken();
		Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
		String url = "/pingpong/admin/games?page=1&seasonId=" + season.getId();

		//when
		String contentAsString = mockMvc.perform(get(url)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
			.andExpect(status().isOk())
			.andReturn().getResponse().getContentAsString();
	}
}
