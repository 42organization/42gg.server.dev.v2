package gg.agenda.api.user.agendaprofile;

import static gg.data.agenda.type.Location.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.agenda.api.AgendaMockData;
import gg.agenda.api.user.agendaprofile.controller.response.AgendaProfileDetailsResDto;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.data.user.User;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class AgendaProfileControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TestDataUtils testDataUtils;
	@Autowired
	private AgendaMockData agendaMockData;
	User user;
	String accessToken;

	@Nested
	@DisplayName("agenda profile 상세 조회")
	class GetAgendaProfile {

		@BeforeEach
		void beforeEach() {
			user = testDataUtils.createNewUser();
			accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		}

		@Test
		@DisplayName("로그인된 유저에 해당하는 Agenda profile를 상세 조회합니다.")
		void test() throws Exception {
			//given
			AgendaProfile agendaProfile = agendaMockData.createAgendaProfile(user, SEOUL);
			Ticket ticket = agendaMockData.createTicket(agendaProfile);

			// when
			String response = mockMvc.perform(get("/agenda/profile")
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

			AgendaProfileDetailsResDto result = objectMapper.readValue(response, AgendaProfileDetailsResDto.class);

			// then
			assertThat(result.getUserIntraId()).isEqualTo(user.getIntraId());
			assertThat(result.getUserContent()).isEqualTo(agendaProfile.getContent());
			assertThat(result.getUserGithub()).isEqualTo(agendaProfile.getGithubUrl());
			assertThat(result.getUserCoalition()).isEqualTo(agendaProfile.getCoalition());
			assertThat(result.getUserLocation()).isEqualTo(agendaProfile.getLocation());
			assertThat(result.getTicketCount()).isEqualTo(1);
		}

		@Test
		@DisplayName("로그인된 유저가 유효하지 않을 때")
		void testInvalidUser() throws Exception {
			// given: 유효하지 않은 유저의 액세스 토큰
			String invalidAccessToken = "invalid-access-token";

			// when & then: 예외가 발생해야 함
			mockMvc.perform(get("/agenda/profile")
					.header("Authorization", "Bearer " + invalidAccessToken))
				.andExpect(status().isUnauthorized());
		}

		@Test
		@DisplayName("해당 로그인 유저의 아젠다 프로필이 없을 때")
		void testAgendaProfileNotFound() throws Exception {
			// given: 특정 유저와 관련된 AgendaProfile이 없음

			// when & then: 예외가 발생해야 함
			mockMvc.perform(get("/agenda/profile")
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNotFound());
		}
	}
}
