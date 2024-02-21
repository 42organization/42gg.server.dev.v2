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

import gg.pingpong.api.admin.game.service.GameAdminService;
import gg.pingpong.api.global.security.jwt.utils.AuthTokenProvider;
import gg.pingpong.data.game.Game;
import gg.pingpong.data.game.Season;
import gg.pingpong.data.game.Team;
import gg.pingpong.data.game.TeamUser;
import gg.pingpong.data.game.type.Mode;
import gg.pingpong.data.user.User;
import gg.pingpong.repo.game.GameRepository;
import gg.pingpong.repo.pchange.PChangeRepository;
import gg.pingpong.repo.rank.redis.RankRedisRepository;
import gg.pingpong.repo.team.TeamRepository;
import gg.pingpong.repo.team.TeamUserRepository;
import gg.pingpong.utils.TestDataUtils;
import gg.pingpong.utils.annotation.IntegrationTest;
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
