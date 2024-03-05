package gg.pingpong.api.user.tournament.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.auth.utils.AuthTokenProvider;
import gg.data.pingpong.game.type.Mode;
import gg.data.pingpong.season.Season;
import gg.data.pingpong.tournament.Tournament;
import gg.data.pingpong.tournament.TournamentGame;
import gg.data.pingpong.tournament.type.TournamentRound;
import gg.data.pingpong.tournament.type.TournamentStatus;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.pingpong.api.user.tournament.controller.response.TournamentGameListResponseDto;
import gg.pingpong.api.user.tournament.controller.response.TournamentGameResDto;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.GameInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TournamentGameControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AuthTokenProvider tokenProvider;

	String accessToken;
	Tournament testTournament;
	String tournamentUrl = "/pingpong/tournaments/";

	@BeforeEach
	void beforeEach() {
		User tester = testDataUtils.createNewUser("findControllerTester", "findControllerTester", RacketType.DUAL,
			SnsType.SLACK, RoleType.ADMIN);
		accessToken = tokenProvider.createToken(tester.getId());

		Season season = testDataUtils.createSeason();
		testTournament = testDataUtils.createTournament("Test Tournament", LocalDateTime.now(),
			LocalDateTime.now().plusHours(2), TournamentStatus.LIVE);
		int idx = 0;
		for (TournamentRound round : TournamentRound.values()) {
			User gamer = testDataUtils.createNewUser("gamer" + idx++);
			GameInfoDto gameInfoDto = testDataUtils.createGame(gamer, LocalDateTime.now().minusDays(10),
				LocalDateTime.now().minusDays(10).plusMinutes(20), season, Mode.TOURNAMENT);
			TournamentGame tournamentGame = testDataUtils.createTournamentGame(testTournament, round, gameInfoDto);
		}
	}

	@Nested
	@DisplayName("토너먼트_게임_리스트_조회")
	class FindTournamentGameTest {

		@Test
		@DisplayName("[Get] pingpong/tournaments/{tournamentId}/games")
		public void getTournamentGames() throws Exception {

			// given
			String url = tournamentUrl + testTournament.getId() + "/games";

			// when
			String contentAsString = mockMvc.perform(get(url).header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			TournamentGameListResponseDto resp = objectMapper.readValue(contentAsString,
				TournamentGameListResponseDto.class);

			// then
			assertThat(resp.getTournamentId()).isEqualTo(testTournament.getId());
			assertThat(resp.getGames().size()).isEqualTo(TournamentRound.values().length);
			for (TournamentGameResDto tournamentGameResDto : resp.getGames()) {
				assertThat(tournamentGameResDto.getTournamentGameId()).isNotNull();
				assertThat(tournamentGameResDto.getTournamentRound()).isNotNull();
				if (!Objects.equals(tournamentGameResDto.getTournamentRound(), TournamentRound.THE_FINAL)) {
					assertThat(tournamentGameResDto.getNextTournamentGameId()).isNotNull();
				}
			}
		}
	}
}
