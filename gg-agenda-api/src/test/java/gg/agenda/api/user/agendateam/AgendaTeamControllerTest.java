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

import gg.agenda.api.AgendaMockData;
import gg.agenda.api.user.agendateam.controller.request.TeamCreateReqDto;
import gg.agenda.api.user.agendateam.controller.request.TeamKeyReqDto;
import gg.agenda.api.user.agendateam.controller.response.TeamCreateResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamDetailsResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.Ticket;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
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
	User seoulUser;
	User gyeongsanUser;
	String seoulUserAccessToken;
	String gyeongsanUserAccessToken;
	AgendaProfile seoulUserAgendaProfile;
	AgendaProfile gyeongsanUserAgendaProfile;

	@Nested
	@DisplayName("팀 생성 테스트")
	class AddTeamTest {
		@BeforeEach
		void beforeEach() {
			seoulUser = testDataUtils.createNewUser();
			seoulUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(seoulUser);
			seoulUserAgendaProfile = agendaMockData.createAgendaProfile(seoulUser, SEOUL);
			gyeongsanUser = testDataUtils.createNewUser();
			gyeongsanUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(gyeongsanUser);
			gyeongsanUserAgendaProfile = agendaMockData.createAgendaProfile(gyeongsanUser, GYEONGSAN);
		}

		@Test
		@DisplayName("201 서울 agenda에 서울 user 팀 생성 성공")
		public void addNewTeamStatusSeoul() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			Ticket ticket = agendaMockData.createTicket(seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
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
			Ticket ticket = agendaMockData.createTicket(gyeongsanUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "GYEONGSAN",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
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
			Ticket ticket = agendaMockData.createTicket(seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
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
			Ticket ticket = agendaMockData.createTicket(gyeongsanUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "GYEONGSAN",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
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
			Ticket ticket = agendaMockData.createTicket(seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "MIX",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
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
			Ticket ticket = agendaMockData.createTicket(gyeongsanUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "MIX",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
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
			Ticket ticket = agendaMockData.createTicket(seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
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
			Ticket ticket = agendaMockData.createTicket(gyeongsanUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "GYEONGSAN",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
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
			Ticket ticket = agendaMockData.createTicket(seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
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
			Ticket ticket = agendaMockData.createTicket(seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
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
			Ticket ticket = agendaMockData.createTicket(gyeongsanUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "GYEONGSAN",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
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
			Agenda agenda = agendaMockData.createAgenda(seoulUser.getIntraId());
			Ticket ticket = agendaMockData.createTicket(seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
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
			Ticket ticket = agendaMockData.createTicket(gyeongsanUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("409 이미 있는 팀 이름으로 인한 실패")
		public void alreadyTeamNameExist() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			Ticket ticket = agendaMockData.createTicket(seoulUserAgendaProfile);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda);
			TeamCreateReqDto req = new TeamCreateReqDto(team.getName(), true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("409 한 agenda에 여러 팀 참가 및 생성 불가로 인한 실패")
		public void alreadyTeamExistForAgenda() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			Ticket ticket = agendaMockData.createTicket(seoulUserAgendaProfile);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser);
			AgendaTeamProfile agendaTeamProfile = agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("newName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andReturn().getResponse().getContentAsString();
		}
	}

	@Nested
	@DisplayName("팀 상세 정보 조회 테스트")
	class AgendaTeamDetails {
		@BeforeEach
		void beforeEach() {
			seoulUser = testDataUtils.createNewUser();
			seoulUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(seoulUser);
			seoulUserAgendaProfile = agendaMockData.createAgendaProfile(seoulUser, SEOUL);
			gyeongsanUser = testDataUtils.createNewUser();
			gyeongsanUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(gyeongsanUser);
			gyeongsanUserAgendaProfile = agendaMockData.createAgendaProfile(gyeongsanUser, GYEONGSAN);
		}

		@Test
		@DisplayName("200 팀 상세 정보 조회 성공")
		public void teamDetailsGetSuccess() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(MIX);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, MIX);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			agendaMockData.createAgendaTeamProfile(team, gyeongsanUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					get("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			TeamDetailsResDto result = objectMapper.readValue(res, TeamDetailsResDto.class);
			// then
			assertThat(result.getTeamName()).isEqualTo(team.getName());
			assertThat(result.getTeamLeaderIntraId()).isEqualTo(seoulUser.getIntraId());
			assertThat(result.getTeamStatus()).isEqualTo(team.getStatus());
			assertThat(result.getTeamLocation()).isEqualTo(team.getLocation());
			assertThat(result.getTeamContent()).isEqualTo(team.getContent());
			assertThat(result.getTeamMates().get(0).getIntraId()).isEqualTo(seoulUser.getIntraId());
			assertThat(result.getTeamMates().get(0).getCoalition()).isEqualTo(seoulUserAgendaProfile.getCoalition());
			assertThat(result.getTeamMates().get(1).getIntraId()).isEqualTo(gyeongsanUser.getIntraId());
			assertThat(result.getTeamMates().get(1).getCoalition()).isEqualTo(
				gyeongsanUserAgendaProfile.getCoalition());
		}

		@Test
		@DisplayName("404 agenda가 없음으로 인한 팀 상세 정보 조회 실패")
		public void teamDetailsGetFailByNoAgenda() throws Exception {
			//given
			TeamKeyReqDto req = new TeamKeyReqDto(UUID.randomUUID());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					get("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", UUID.randomUUID().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("404 team이 없음으로 인한 팀 상세 정보 조회 실패")
		public void teamDetailsGetFailByNoTeam() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(MIX);
			TeamKeyReqDto req = new TeamKeyReqDto(UUID.randomUUID());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					get("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("403 조회 불가능한 team으로 인한 팀 상세 정보 조회 실패")
		public void teamDetailsGetFailByConfirmTeam() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(AgendaStatus.CONFIRM);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, MIX, AgendaTeamStatus.CONFIRM);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					get("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("404 조회 불가능한 team으로 인한 팀 상세 정보 조회 실패")
		public void teamDetailsGetFailByCancelTeam() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(AgendaStatus.CONFIRM);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, MIX, AgendaTeamStatus.CANCEL);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					get("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();
		}
	}

	@Nested
	@DisplayName("내 팀 조회 테스트")
	class MyTeamTest {
		@BeforeEach
		void beforeEach() {
			seoulUser = testDataUtils.createNewUser();
			seoulUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(seoulUser);
			seoulUserAgendaProfile = agendaMockData.createAgendaProfile(seoulUser, SEOUL);
			gyeongsanUser = testDataUtils.createNewUser();
			gyeongsanUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(gyeongsanUser);
			gyeongsanUserAgendaProfile = agendaMockData.createAgendaProfile(gyeongsanUser, GYEONGSAN);
		}

		@Test
		@DisplayName("200 서울 agenda에 서울 user 팀 조회 성공")
		public void myTeamSimpleDetailsStatusSeoul() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			// when
			String res = mockMvc.perform(
					get("/agenda/team/my")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString()))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			// then
			assertThat(res).isNotNull();
		}

		@Test
		@DisplayName("200 경산 agenda에 경산 user 팀 조회 성공")
		public void myTeamSimpleDetailsStatusGyeongsan() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(GYEONGSAN);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, gyeongsanUser);
			agendaMockData.createAgendaTeamProfile(team, gyeongsanUserAgendaProfile);
			// when
			String res = mockMvc.perform(
					get("/agenda/team/my")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString()))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			// then
			assertThat(res).isNotNull();
		}

		@Test
		@DisplayName("204 my팀 없을때 조회 성공")
		public void myTeamSimpleDetailsStatusNoTeam() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			// when
			String res = mockMvc.perform(
					get("/agenda/team/my")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString()))
				.andExpect(status().isNoContent())
				.andReturn().getResponse().getContentAsString();
			// then
			assertThat(res).isNotNull();
		}

		@Test
		@DisplayName("404 agenda 없음으로 인한 실패")
		public void noAgendaFail() throws Exception {
			//given
			UUID noAgendaKey = UUID.randomUUID();
			// when && then
			String res = mockMvc.perform(
					get("/agenda/team/my")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", noAgendaKey.toString()))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("404 agenda에 프로필 없음으로 인한 실패")
		public void noAgendaProfileFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			User noProfileUser = testDataUtils.createNewUser();
			String noProfileUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(noProfileUser);
			// when && then
			String res = mockMvc.perform(
					get("/agenda/team/my")
						.header("Authorization", "Bearer " + noProfileUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString()))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();
		}
	}

	@Nested
	@DisplayName("팀 CONFIRM 테스트")
	class ConfirmTeamTest {
		@BeforeEach
		void beforeEach() {
			seoulUser = testDataUtils.createNewUser();
			seoulUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(seoulUser);
			seoulUserAgendaProfile = agendaMockData.createAgendaProfile(seoulUser, SEOUL);
			gyeongsanUser = testDataUtils.createNewUser();
			gyeongsanUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(gyeongsanUser);
			gyeongsanUserAgendaProfile = agendaMockData.createAgendaProfile(gyeongsanUser, GYEONGSAN);
		}

		@Test
		@DisplayName("200 팀 CONFIRM 성공")
		public void confirmTeamSuccess() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			// then
			AgendaTeam updatedTeam = agendaTeamRepository.findByTeamKey(team.getTeamKey()).orElse(null);
			assertThat(updatedTeam.getStatus()).isEqualTo(AgendaTeamStatus.CONFIRM);
		}

		@Test
		@DisplayName("404 agenda 없음으로 인한 실패")
		public void noAgendaFail() throws Exception {
			//given
			UUID noAgendaKey = UUID.randomUUID();
			UUID noTeamKey = UUID.randomUUID();
			TeamKeyReqDto req = new TeamKeyReqDto(noTeamKey);
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", noAgendaKey.toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("404 team 없음으로 인한 실패")
		public void noTeamFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			TeamKeyReqDto req = new TeamKeyReqDto(UUID.randomUUID());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("403 호스트가 아님으로 인한 실패")
		public void notValidTeamHostFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(seoulUser.getIntraId());
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL, AgendaTeamStatus.CONFIRM);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			User notHostUser = testDataUtils.createNewUser();
			String notHostUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(notHostUser);
			agendaMockData.createAgendaTeamProfile(team, agendaMockData.createAgendaProfile(notHostUser, SEOUL));
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + notHostUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("404 OPEN 상태가 아닌 팀으로 인한 실패 -> 서비스 로직에서 처리됨, 엔티티는 별개")
		public void notValidTeamStatusFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL, AgendaTeamStatus.CANCEL);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("409 이미 CONFIRM된 팀으로 인한 실패")
		public void alreadyConfirmTeamFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL, AgendaTeamStatus.CONFIRM);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("403 참여 불가능한 Agenda Status 으로 인한 실패")
		public void notValidAgendaStatus() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(AgendaStatus.CONFIRM);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("403 참여 불가능한 Agenda Location 으로 인한 실패")
		public void notValidAgendaLocation() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(GYEONGSAN);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();
		}

		@Test
		@DisplayName("400 참여 불가능한 Agenda Team 인원으로 인한 실패")
		public void notValidAgendaTeam() throws Exception {
			//given
			Agenda agenda = agendaMockData.createNeedMorePeopleAgenda(5);
			AgendaTeam team = agendaMockData.createAgendaTeam(2, agenda, seoulUser, SEOUL);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			User notHostUser = testDataUtils.createNewUser();
			agendaMockData.createAgendaTeamProfile(team, agendaMockData.createAgendaProfile(notHostUser, SEOUL));
			String content = objectMapper.writeValueAsString(req);
			// when && then
			String res = mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn().getResponse().getContentAsString();
		}
	}
}
