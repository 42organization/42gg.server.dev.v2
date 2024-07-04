package gg.agenda.api.user.agendateam;

import static gg.data.agenda.type.Location.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

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

import gg.agenda.api.user.AgendaMockData;
import gg.agenda.api.user.agendateam.controller.request.TeamCreateReqDto;
import gg.agenda.api.user.agendateam.controller.response.TeamCreateResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.Ticket;
import gg.data.agenda.type.AgendaStatus;
import gg.data.user.User;
import gg.repo.agenda.AgendaTeamRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class AgendaTeamControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TestDataUtils testDataUtils;
	@Autowired
	private AgendaMockData agendaMockData;
	@Autowired
	AgendaTeamRepository agendaTeamRepository;
	User user;
	User anotherUser;
	String accessToken;
	String anotherAccessToken;
	AgendaProfile agendaProfile;
	AgendaProfile anotherAgendaProfile;

	@Nested
	@DisplayName("팀 생성 테스트")
	class addTeamTest {
		@BeforeEach
		void beforeEach() {
			user = testDataUtils.createNewUser();
			accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
			agendaProfile = agendaMockData.createAgendaProfile(user, SEOUL);
			anotherUser = testDataUtils.createNewUser();
			anotherAccessToken = testDataUtils.getLoginAccessTokenFromUser(anotherUser);
			anotherAgendaProfile = agendaMockData.createAgendaProfile(anotherUser, GYEONGSAN);
		}

		@Test
		@DisplayName("201 서울 agenda에 서울 user 팀 생성 성공")
		public void addNewTeamStatusSeoul() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			Ticket ticket = agendaMockData.createTicket(agendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + accessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			TeamCreateResDto result = objectMapper.readValue(res, TeamCreateResDto.class);
			// then
			AgendaTeam createdTeam = agendaTeamRepository.findByTeamKey(UUID.fromString(result.getTeamKey()))
				.orElse(null);
			assertThat(createdTeam).isNotNull();
			assertThat(createdTeam.getName()).isEqualTo("teamName");
			assertThat(createdTeam.getLocation()).isEqualTo(SEOUL);
			assertThat(createdTeam.getContent()).isEqualTo("teamContent");
		}

		@Test
		@DisplayName("201 경산 agenda에 경산 user 팀 생성 성공")
		public void addNewTeamStatusGyeongsan() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(GYEONGSAN);
			Ticket ticket = agendaMockData.createTicket(anotherAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "GYEONGSAN", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + anotherAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			TeamCreateResDto result = objectMapper.readValue(res, TeamCreateResDto.class);
			// then
			AgendaTeam createdTeam = agendaTeamRepository.findByTeamKey(UUID.fromString(result.getTeamKey()))
				.orElse(null);
			assertThat(createdTeam).isNotNull();
			assertThat(createdTeam.getName()).isEqualTo("teamName");
			assertThat(createdTeam.getLocation()).isEqualTo(GYEONGSAN);
			assertThat(createdTeam.getContent()).isEqualTo("teamContent");
		}

		@Test
		@DisplayName("201 mix agenda에 서울 user 팀 생성 성공")
		public void addNewTeamStatusMixFromSeoul() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(MIX);
			Ticket ticket = agendaMockData.createTicket(agendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + accessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			TeamCreateResDto result = objectMapper.readValue(res, TeamCreateResDto.class);
			// then
			AgendaTeam createdTeam = agendaTeamRepository.findByTeamKey(UUID.fromString(result.getTeamKey()))
				.orElse(null);
			assertThat(createdTeam).isNotNull();
			assertThat(createdTeam.getName()).isEqualTo("teamName");
			assertThat(createdTeam.getLocation()).isEqualTo(SEOUL);
			assertThat(createdTeam.getContent()).isEqualTo("teamContent");
		}

		@Test
		@DisplayName("201 MIX agenda에 경산 user 팀 생성 성공")
		public void addNewTeamStatusMixFromGyeongsan() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(MIX);
			Ticket ticket = agendaMockData.createTicket(anotherAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "GYEONGSAN", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + anotherAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			TeamCreateResDto result = objectMapper.readValue(res, TeamCreateResDto.class);
			// then
			AgendaTeam createdTeam = agendaTeamRepository.findByTeamKey(UUID.fromString(result.getTeamKey()))
				.orElse(null);
			assertThat(createdTeam).isNotNull();
			assertThat(createdTeam.getName()).isEqualTo("teamName");
			assertThat(createdTeam.getLocation()).isEqualTo(GYEONGSAN);
			assertThat(createdTeam.getContent()).isEqualTo("teamContent");
		}

		@Test
		@DisplayName("201 mix agenda에 서울 user가 mix 팀 생성 성공")
		public void addNewTeamStatusMixFromMixToSeoul() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(MIX);
			Ticket ticket = agendaMockData.createTicket(agendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "MIX", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + accessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			TeamCreateResDto result = objectMapper.readValue(res, TeamCreateResDto.class);
			// then
			AgendaTeam createdTeam = agendaTeamRepository.findByTeamKey(UUID.fromString(result.getTeamKey()))
				.orElse(null);
			assertThat(createdTeam).isNotNull();
			assertThat(createdTeam.getName()).isEqualTo("teamName");
			assertThat(createdTeam.getLocation()).isEqualTo(MIX);
			assertThat(createdTeam.getContent()).isEqualTo("teamContent");
		}

		@Test
		@DisplayName("201 MIX agenda에 경산 user가 mix 팀 생성 성공")
		public void addNewTeamStatusMixFromMixToGyeongsan() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(MIX);
			Ticket ticket = agendaMockData.createTicket(anotherAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "MIX", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + anotherAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			TeamCreateResDto result = objectMapper.readValue(res, TeamCreateResDto.class);
			// then
			AgendaTeam createdTeam = agendaTeamRepository.findByTeamKey(UUID.fromString(result.getTeamKey()))
				.orElse(null);
			assertThat(createdTeam).isNotNull();
			assertThat(createdTeam.getName()).isEqualTo("teamName");
			assertThat(createdTeam.getLocation()).isEqualTo(MIX);
			assertThat(createdTeam.getContent()).isEqualTo("teamContent");
		}

		@Test
		@DisplayName("404 아젠다 없음으로 인한 실패")
		public void noAgendaFail() throws Exception {
			//given
			UUID noAgendaKey = UUID.randomUUID();
			Ticket ticket = agendaMockData.createTicket(agendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + accessToken)
						.param("agenda_key", noAgendaKey.toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("400 참여 불가능한 Agenda Location 으로 인한 실패")
		public void notValidAgendaLocation() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			Ticket ticket = agendaMockData.createTicket(anotherAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "GYEONGSAN", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + anotherAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("400 참여 불가능한 Agenda Status 으로 인한 실패")
		public void notValidAgendaStatus() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(AgendaStatus.CONFIRM);
			Ticket ticket = agendaMockData.createTicket(agendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + accessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("403 참여 불가능한 Agenda Team 한도로 인한 실패")
		public void notValidAgendaTeam() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(5);
			Ticket ticket = agendaMockData.createTicket(agendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + accessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("400 참여 불가능한 Agenda 시간으로 인한 실패")
		public void notValidAgendaDeadline() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			Ticket ticket = agendaMockData.createTicket(anotherAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "GYEONGSAN", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + anotherAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("403 아젠다 호스트의 팀 생성으로 인한 실패")
		public void agendaHostFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			Ticket ticket = agendaMockData.createTicket(agendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + accessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("400 참여 불가능한 유저의 Location 으로 인한 실패")
		public void notValidUserLocation() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			Ticket ticket = agendaMockData.createTicket(anotherAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL", "teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + anotherAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();
		}
	}
}
