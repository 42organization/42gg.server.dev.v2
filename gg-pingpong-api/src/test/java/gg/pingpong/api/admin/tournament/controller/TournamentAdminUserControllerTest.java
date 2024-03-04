package gg.pingpong.api.admin.tournament.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.data.tournament.Tournament;
import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;
import gg.auth.utils.AuthTokenProvider;
import gg.pingpong.api.user.tournament.controller.response.TournamentUserListResponseDto;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.extern.slf4j.Slf4j;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class TournamentAdminUserControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	TestDataUtils testDataUtils;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AuthTokenProvider tokenProvider;

	String accessToken;
	final int joinUserCnt = 8;
	final int notJoinUserCnt = 4;
	String testName = "42_gg_tester_";
	Tournament tournament;
	String adminUrl = "/pingpong/admin/tournaments/";

	@BeforeEach
	void beforeEach() {
		User tester = testDataUtils.createNewUser("findControllerTester", "findControllerTester", RacketType.DUAL,
			SnsType.SLACK, RoleType.ADMIN);
		accessToken = tokenProvider.createToken(tester.getId());
		tournament = testDataUtils.createTournamentWithUser(joinUserCnt, notJoinUserCnt, testName);
	}

	@Nested
	@DisplayName("/pingpong/admin/tournaments")
	class FindTournamentUser {

		@Test
		@DisplayName("[Get] /{tournamentId}/users")
		void getAllTournamentUser() throws Exception {
			// given

			String url = adminUrl + tournament.getId() + "/users";

			// when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			TournamentUserListResponseDto resp = objectMapper.readValue(contentAsString,
				TournamentUserListResponseDto.class);

			// then
			assertThat(resp.getUsers().size()).isEqualTo(joinUserCnt + notJoinUserCnt);
			for (int i = 0; i < joinUserCnt + notJoinUserCnt; i++) {
				assertThat(resp.getUsers().get(i).getIntraId()).isEqualTo(testName + i);
			}
		}

		@Test
		@DisplayName("[Get] /{tournamentId}/users?isJoined=true")
		void getAllTournamentUserByIsJoined() throws Exception {

			// given
			String url = adminUrl + tournament.getId() + "/users" + "?isJoined=true";

			// when
			String contentAsString = mockMvc.perform(
					get(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			TournamentUserListResponseDto resp = objectMapper.readValue(contentAsString,
				TournamentUserListResponseDto.class);

			// then
			assertThat(resp.getUsers().size()).isEqualTo(joinUserCnt);
			for (int i = 0; i < joinUserCnt; i++) {
				assertThat(resp.getUsers().get(i).getIntraId()).isEqualTo(testName + i);
				assertThat(resp.getUsers().get(i).getIsJoined()).isEqualTo(true);
			}
		}
	}
}
