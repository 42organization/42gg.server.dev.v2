package com.gg.server.domain.tournament.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
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
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.tournament.data.Tournament;
import com.gg.server.domain.tournament.data.TournamentGame;
import com.gg.server.domain.tournament.dto.TournamentGameListResponseDto;
import com.gg.server.domain.tournament.dto.TournamentGameResDto;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.user.controller.dto.GameInfoDto;
import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;
import com.gg.server.global.security.jwt.utils.AuthTokenProvider;
import com.gg.server.utils.TestDataUtils;
import com.gg.server.utils.annotation.IntegrationTest;

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
