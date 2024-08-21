package gg.agenda.api.user.agendateam;

import static gg.data.agenda.type.AgendaStatus.*;
import static gg.data.agenda.type.Location.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gg.agenda.api.AgendaMockData;
import gg.agenda.api.user.agendateam.controller.request.TeamCreateReqDto;
import gg.agenda.api.user.agendateam.controller.request.TeamKeyReqDto;
import gg.agenda.api.user.agendateam.controller.request.TeamUpdateReqDto;
import gg.agenda.api.user.agendateam.controller.response.ConfirmTeamResDto;
import gg.agenda.api.user.agendateam.controller.response.OpenTeamResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamDetailsResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamKeyResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.user.User;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.repo.agenda.TicketRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;
import gg.utils.fixture.agenda.AgendaFixture;
import gg.utils.fixture.agenda.AgendaTeamFixture;
import gg.utils.fixture.agenda.AgendaTeamProfileFixture;
import gg.utils.fixture.agenda.TicketFixture;

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
	private TicketRepository ticketRepository;
	@Autowired
	private AgendaTeamRepository agendaTeamRepository;
	@Autowired
	private AgendaTeamProfileRepository agendaTeamProfileRepository;
	@Autowired
	private AgendaFixture agendaFixture;
	@Autowired
	private AgendaTeamFixture agendaTeamFixture;
	@Autowired
	private AgendaTeamProfileFixture agendaTeamProfileFixture;
	@Autowired
	private TicketFixture ticketFixture;
	User seoulUser;
	User gyeongsanUser;
	User anotherSeoulUser;
	String seoulUserAccessToken;
	String gyeongsanUserAccessToken;
	String anotherSeoulUserAccessToken;
	AgendaProfile seoulUserAgendaProfile;
	AgendaProfile gyeongsanUserAgendaProfile;
	AgendaProfile anotherSeoulUserAgendaProfile;

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
			agendaMockData.createTicket(seoulUserAgendaProfile);
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
			TeamKeyResDto result = objectMapper.readValue(res, TeamKeyResDto.class);
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
			agendaMockData.createTicket(gyeongsanUserAgendaProfile);
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
			TeamKeyResDto result = objectMapper.readValue(res, TeamKeyResDto.class);
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
			agendaMockData.createTicket(seoulUserAgendaProfile);
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
			TeamKeyResDto result = objectMapper.readValue(res, TeamKeyResDto.class);
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
			agendaMockData.createTicket(gyeongsanUserAgendaProfile);
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
			TeamKeyResDto result = objectMapper.readValue(res, TeamKeyResDto.class);
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
			agendaMockData.createTicket(seoulUserAgendaProfile);
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
			TeamKeyResDto result = objectMapper.readValue(res, TeamKeyResDto.class);
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
			agendaMockData.createTicket(gyeongsanUserAgendaProfile);
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
			TeamKeyResDto result = objectMapper.readValue(res, TeamKeyResDto.class);
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
			agendaMockData.createTicket(seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", noAgendaKey.toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("400 참여 불가능한 Agenda Location 으로 인한 실패")
		public void notValidAgendaLocation() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			agendaMockData.createTicket(gyeongsanUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "GYEONGSAN",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("400 참여 불가능한 Agenda Status 으로 인한 실패")
		public void notValidAgendaStatus() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(FINISH);
			agendaMockData.createTicket(seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("403 참여 불가능한 Agenda Team 한도로 인한 실패")
		public void notValidAgendaTeam() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(5);
			agendaMockData.createTicket(seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("400 참여 불가능한 Agenda 시간으로 인한 실패")
		public void notValidAgendaDeadline() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			agendaMockData.createTicket(gyeongsanUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "GYEONGSAN",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("403 아젠다 호스트의 팀 생성으로 인한 실패")
		public void agendaHostFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(seoulUser.getIntraId());
			agendaMockData.createTicket(seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("400 참여 불가능한 유저의 Location 으로 인한 실패")
		public void notValidUserLocation() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			agendaMockData.createTicket(gyeongsanUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("teamName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("409 이미 있는 팀 이름으로 인한 실패")
		public void alreadyTeamNameExist() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			agendaMockData.createTicket(seoulUserAgendaProfile);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda);
			TeamCreateReqDto req = new TeamCreateReqDto(team.getName(), true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
		}

		@Test
		@DisplayName("409 한 agenda에 여러 팀 참가 및 생성 불가로 인한 실패")
		public void alreadyTeamExistForAgenda() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			agendaMockData.createTicket(seoulUserAgendaProfile);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamCreateReqDto req = new TeamCreateReqDto("newName", true, "SEOUL",
				"teamContent");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
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
			// when
			String res = mockMvc.perform(
					get("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
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
			// when && then
			mockMvc.perform(
					get("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", UUID.randomUUID().toString())
						.param("teamKey", UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("404 team이 없음으로 인한 팀 상세 정보 조회 실패")
		public void teamDetailsGetFailByNoTeam() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(MIX);
			TeamKeyReqDto req = new TeamKeyReqDto(UUID.randomUUID());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					get("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("403 조회 불가능한 team으로 인한 팀 상세 정보 조회 실패")
		public void teamDetailsGetFailByConfirmTeam() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(FINISH);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, MIX, AgendaTeamStatus.CONFIRM);
			// when && then
			mockMvc.perform(
					get("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("404 조회 불가능한 team으로 인한 팀 상세 정보 조회 실패")
		public void teamDetailsGetFailByCancelTeam() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(FINISH);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, MIX, AgendaTeamStatus.CANCEL);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			// when && then
			mockMvc.perform(
					get("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
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
			mockMvc.perform(
					get("/agenda/team/my")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", noAgendaKey.toString()))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("404 agenda에 프로필 없음으로 인한 실패")
		public void noAgendaProfileFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			User noProfileUser = testDataUtils.createNewUser();
			String noProfileUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(noProfileUser);
			// when && then
			mockMvc.perform(
					get("/agenda/team/my")
						.header("Authorization", "Bearer " + noProfileUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString()))
				.andExpect(status().isNotFound());
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
			// when
			mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
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
			// when && then
			mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", noAgendaKey.toString())
						.param("teamKey", noTeamKey.toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("404 team 없음으로 인한 실패")
		public void noTeamFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			// when && then
			mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
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
			// when && then
			mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + notHostUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("404 OPEN 상태가 아닌 팀으로 인한 실패 -> 서비스 로직에서 처리됨, 엔티티는 별개")
		public void notValidTeamStatusFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL, AgendaTeamStatus.CANCEL);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			// when && then
			mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("400 이미 CONFIRM된 팀으로 인한 실패")
		public void alreadyConfirmTeamFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL, AgendaTeamStatus.CONFIRM);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("403 참여 불가능한 Agenda Status 으로 인한 실패")
		public void notValidAgendaStatus() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(FINISH);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
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
			mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
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
			mockMvc.perform(
					patch("/agenda/team/confirm")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("팀 Leave 테스트")
	class LeaveTeamTest {
		@BeforeEach
		void beforeEach() {
			seoulUser = testDataUtils.createNewUser();
			seoulUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(seoulUser);
			seoulUserAgendaProfile = agendaMockData.createAgendaProfile(seoulUser, SEOUL);
			anotherSeoulUser = testDataUtils.createNewUser();
			anotherSeoulUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(anotherSeoulUser);
			anotherSeoulUserAgendaProfile = agendaMockData.createAgendaProfile(anotherSeoulUser, SEOUL);
			gyeongsanUser = testDataUtils.createNewUser();
			gyeongsanUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(gyeongsanUser);
			gyeongsanUserAgendaProfile = agendaMockData.createAgendaProfile(gyeongsanUser, GYEONGSAN);
		}

		@Test
		@DisplayName("200 팀원 팀 나가기 성공")
		public void leaveTeamMateSuccess() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, 2);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			AgendaTeamProfile atp = agendaMockData.createAgendaTeamProfile(team, anotherSeoulUserAgendaProfile);
			// when
			mockMvc.perform(
					patch("/agenda/team/cancel")
						.header("Authorization", "Bearer " + anotherSeoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
			// then
			AgendaTeam updatedTeam = agendaTeamRepository.findByTeamKey(team.getTeamKey()).orElse(null);
			assert updatedTeam != null;
			assertThat(updatedTeam.getMateCount()).isEqualTo(1);
			assertThat(agenda.getCurrentTeam()).isEqualTo(1);
			AgendaTeamProfile updatedAtp = agendaTeamProfileRepository.findById(atp.getId()).orElse(null);
			assert updatedAtp != null;
			assertThat(updatedAtp.getIsExist()).isFalse();
			ticketRepository.findFirstByAgendaProfileAndIsApprovedTrueAndIsUsedFalseOrderByCreatedAtAsc(
					updatedAtp.getProfile())
				.ifPresent(ticket -> {
					assertThat(ticket.getUsedTo()).isNull();
				});
		}

		@Test
		@DisplayName("200 팀리더 팀 나가기 성공")
		public void leaveTeamLeaderSuccess() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, 2);
			AgendaTeamProfile atpLeader = agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			AgendaTeamProfile atp = agendaMockData.createAgendaTeamProfile(team, anotherSeoulUserAgendaProfile);
			// when
			mockMvc.perform(
					patch("/agenda/team/cancel")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
			// then
			AgendaTeam updatedTeam = agendaTeamRepository.findByTeamKey(team.getTeamKey()).orElse(null);
			assert updatedTeam != null;
			assertThat(updatedTeam.getMateCount()).isEqualTo(0);
			assertThat(agenda.getCurrentTeam()).isEqualTo(1);
			AgendaTeamProfile updatedAtp = agendaTeamProfileRepository.findById(atp.getId()).orElse(null);
			assert updatedAtp != null;
			assertThat(updatedAtp.getIsExist()).isFalse();
			AgendaTeamProfile updatedAtpLeader = agendaTeamProfileRepository.findById(atpLeader.getId()).orElse(null);
			assert updatedAtpLeader != null;
			assertThat(updatedAtpLeader.getIsExist()).isFalse();
			ticketRepository.findFirstByAgendaProfileAndIsApprovedTrueAndIsUsedFalseOrderByCreatedAtAsc(
					updatedAtp.getProfile())
				.ifPresent(ticket -> {
					assertThat(ticket.getUsedTo()).isNull();
				});
			ticketRepository.findFirstByAgendaProfileAndIsApprovedTrueAndIsUsedFalseOrderByCreatedAtAsc(
					updatedAtpLeader.getProfile())
				.ifPresent(ticket -> {
					assertThat(ticket.getUsedTo()).isNull();
				});
		}

		@Test
		@DisplayName("200 Confirm 팀 리더 팀 나가기 성공")
		public void leaveTeamLeaderTeamStatusConfirmSuccess() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL, AgendaTeamStatus.CONFIRM);
			AgendaTeamProfile atpLeader = agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			AgendaTeamProfile atp = agendaMockData.createAgendaTeamProfile(team, anotherSeoulUserAgendaProfile);
			// when
			mockMvc.perform(
					patch("/agenda/team/cancel")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
			// then
			AgendaTeam updatedTeam = agendaTeamRepository.findByTeamKey(team.getTeamKey()).orElse(null);
			assert updatedTeam != null;
			assertThat(updatedTeam.getMateCount()).isEqualTo(0);
			assertThat(agenda.getCurrentTeam()).isEqualTo(0);
			AgendaTeamProfile updatedAtp = agendaTeamProfileRepository.findById(atp.getId()).orElse(null);
			assert updatedAtp != null;
			assertThat(updatedAtp.getIsExist()).isFalse();
			AgendaTeamProfile updatedAtpLeader = agendaTeamProfileRepository.findById(atpLeader.getId()).orElse(null);
			assert updatedAtpLeader != null;
			assertThat(updatedAtpLeader.getIsExist()).isFalse();
			ticketRepository.findFirstByAgendaProfileAndIsApprovedTrueAndIsUsedFalseOrderByCreatedAtAsc(
					updatedAtp.getProfile())
				.ifPresent(ticket -> {
					assertThat(ticket.getUsedTo()).isNull();
				});
			ticketRepository.findFirstByAgendaProfileAndIsApprovedTrueAndIsUsedFalseOrderByCreatedAtAsc(
					updatedAtpLeader.getProfile())
				.ifPresent(ticket -> {
					assertThat(ticket.getUsedTo()).isNull();
				});
		}

		@Test
		@DisplayName("404 agenda 없음으로 인한 실패")
		public void noAgendaFail() throws Exception {
			//given
			UUID noAgendaKey = UUID.randomUUID();
			UUID noTeamKey = UUID.randomUUID();
			// when && then
			mockMvc.perform(
					patch("/agenda/team/cancel")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", noAgendaKey.toString())
						.param("teamKey", noTeamKey.toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("404 team 없음으로 인한 실패")
		public void noTeamFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			// when && then
			mockMvc.perform(
					patch("/agenda/team/cancel")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("400 탈퇴 불가능한 AgendaTeam Status로 인한 팀장의 나가기 실패")
		public void notValidAgendaTeamStatusWhenTeamLeader() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL, AgendaTeamStatus.CANCEL);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			// when && then
			mockMvc.perform(
					patch("/agenda/team/cancel")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("400 탈퇴 불가능한 AgendaTeam Status로 인한 팀원의 나가기 실패")
		public void notValidAgendaTeamStatusConfirmWhenTeamMate() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL, AgendaTeamStatus.CONFIRM);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			AgendaTeamProfile atp = agendaMockData.createAgendaTeamProfile(team, anotherSeoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team/cancel")
						.header("Authorization", "Bearer " + anotherSeoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("400 탈퇴 불가능한 AgendaTeam Status로 인한 팀원의 나가기 실패")
		public void notValidAgendaTeamStatusCoiWhenTeamMate() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL, AgendaTeamStatus.CONFIRM);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			AgendaTeamProfile atp = agendaMockData.createAgendaTeamProfile(team, anotherSeoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team/cancel")
						.header("Authorization", "Bearer " + anotherSeoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("400 탈퇴 불가능한 Agenda 시간으로 인한 실패")
		public void notValidAgendaDeadline() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(LocalDateTime.now().minusHours(1));
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team/cancel")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("403 탈퇴 불가능한 Agenda Status 으로 인한 실패")
		public void notValidAgendaStatus() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(FINISH);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, SEOUL);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team/cancel")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("403 팀원이 아님으로 인한 실패")
		public void notTeamMateFail() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			AgendaTeam team = agendaMockData.createAgendaTeam(agenda, seoulUser, 2);
			agendaMockData.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			// when && then
			mockMvc.perform(
					patch("/agenda/team/cancel")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		}
	}

	@Nested
	@DisplayName("OPEN팀 조회 테스트")
	class OpenTeamListTest {
		@BeforeEach
		void beforeEach() {
			seoulUser = testDataUtils.createNewUser();
			seoulUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(seoulUser);
			seoulUserAgendaProfile = agendaMockData.createAgendaProfile(seoulUser, SEOUL);
			gyeongsanUser = testDataUtils.createNewUser();
			gyeongsanUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(gyeongsanUser);
			gyeongsanUserAgendaProfile = agendaMockData.createAgendaProfile(gyeongsanUser, GYEONGSAN);
		}

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3, 4, 5})
		@DisplayName("200 OPEN팀 조회 성공")
		public void openTeamGetSuccess(int page) throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			List<AgendaTeam> teams = agendaMockData.createAgendaTeamList(agenda, 23, AgendaTeamStatus.OPEN);
			PageRequestDto req = new PageRequestDto(page, 5);
			// when
			String res = mockMvc.perform(
					get("/agenda/team/open/list")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("page", String.valueOf(req.getPage()))
						.param("size", String.valueOf(req.getSize())))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PageResponseDto<OpenTeamResDto> pageResponseDto = objectMapper
				.readValue(res, new TypeReference<>() {
				});
			List<OpenTeamResDto> result = pageResponseDto.getContent();

			// then
			assertThat(result.size()).isEqualTo(((page - 1) * 5) < teams.size()
				? Math.min(5, teams.size() - (page - 1) * 5) : 0);
			teams.sort((a, b) -> b.getId().compareTo(a.getId()));
			for (int i = 0; i < result.size(); i++) {
				assertThat(result.get(i).getTeamName()).isEqualTo(teams.get((page - 1) * 5 + i).getName());
			}
		}

		@Test
		@DisplayName("200 OPEN팀 없을때 조회 성공")
		public void openTeamGetSuccessNoTeam() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			PageRequestDto req = new PageRequestDto(1, 5);
			// when
			String res = mockMvc.perform(
					get("/agenda/team/open/list")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("page", String.valueOf(req.getPage()))
						.param("size", String.valueOf(req.getSize())))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PageResponseDto<OpenTeamResDto> pageResponseDto = objectMapper
				.readValue(res, new TypeReference<>() {
				});
			List<OpenTeamResDto> result = pageResponseDto.getContent();

			// then
			assertThat(result.size()).isEqualTo(0);
		}

		@Test
		@DisplayName("404 agenda 없음으로 인한 실패")
		public void noAgendaFail() throws Exception {
			//given
			UUID noAgendaKey = UUID.randomUUID();
			PageRequestDto req = new PageRequestDto(1, 5);
			// when && then
			mockMvc.perform(
					get("/agenda/team/open/list")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", noAgendaKey.toString())
						.param("page", String.valueOf(req.getPage()))
						.param("size", String.valueOf(req.getSize())))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("CONFIRM팀 조회 테스트")
	class ConfirmTeamListTest {
		@BeforeEach
		void beforeEach() {
			seoulUser = testDataUtils.createNewUser();
			seoulUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(seoulUser);
			seoulUserAgendaProfile = agendaMockData.createAgendaProfile(seoulUser, SEOUL);
			gyeongsanUser = testDataUtils.createNewUser();
			gyeongsanUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(gyeongsanUser);
			gyeongsanUserAgendaProfile = agendaMockData.createAgendaProfile(gyeongsanUser, GYEONGSAN);

		}

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3, 4, 5})
		@DisplayName("200 CONFIRM팀 조회 성공")
		public void confirmTeamGetSuccess(int page) throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			List<AgendaTeam> teams = agendaMockData.createAgendaTeamList(agenda, 23, AgendaTeamStatus.CONFIRM);
			PageRequestDto req = new PageRequestDto(page, 5);
			// when
			String res = mockMvc.perform(
					get("/agenda/team/confirm/list")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("page", String.valueOf(req.getPage()))
						.param("size", String.valueOf(req.getSize())))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PageResponseDto<ConfirmTeamResDto> pageResponseDto = objectMapper
				.readValue(res, new TypeReference<>() {
				});
			List<ConfirmTeamResDto> result = pageResponseDto.getContent();

			// then
			assertThat(result.size()).isEqualTo(((page - 1) * 5) < teams.size()
				? Math.min(5, teams.size() - (page - 1) * 5) : 0);
			teams.sort((a, b) -> b.getId().compareTo(a.getId()));
			for (int i = 0; i < result.size(); i++) {
				assertThat(result.get(i).getTeamName()).isEqualTo(teams.get((page - 1) * 5 + i).getName());
			}
		}

		@Test
		@DisplayName("200 CONFIRM팀 없을때 조회 성공")
		public void confirmTeamGetSuccessNoTeam() throws Exception {
			//given
			Agenda agenda = agendaMockData.createAgenda(SEOUL);
			PageRequestDto req = new PageRequestDto(1, 5);
			String content = objectMapper.writeValueAsString(req);
			// when
			String res = mockMvc.perform(
					get("/agenda/team/confirm/list")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("page", String.valueOf(req.getPage()))
						.param("size", String.valueOf(req.getSize())))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PageResponseDto<ConfirmTeamResDto> pageResponseDto = objectMapper
				.readValue(res, new TypeReference<>() {
				});
			List<ConfirmTeamResDto> result = pageResponseDto.getContent();

			// then
			assertThat(result.size()).isEqualTo(0);
		}

		@Test
		@DisplayName("404 agenda 없음으로 인한 실패")
		public void noAgendaFail() throws Exception {
			//given
			UUID noAgendaKey = UUID.randomUUID();
			PageRequestDto req = new PageRequestDto(1, 5);
			// when && then
			mockMvc.perform(
					get("/agenda/team/confirm/list")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", noAgendaKey.toString())
						.param("page", String.valueOf(req.getPage()))
						.param("size", String.valueOf(req.getSize())))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("팀 참가 신청 테스트")
	class ApplyTeamTest {
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
		@DisplayName("201 팀 참가 신청 성공")
		public void applyTeamSuccess() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(SEOUL);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, SEOUL);
			ticketFixture.createTicket(seoulUserAgendaProfile);
			// when
			mockMvc.perform(
					post("/agenda/team/join")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
			// then
			AgendaTeam updatedTeam = agendaTeamRepository.findByTeamKey(team.getTeamKey()).orElse(null);
			assert updatedTeam != null;
			assertThat(updatedTeam.getMateCount()).isEqualTo(2);
			AgendaTeamProfile updatedAtp = agendaTeamProfileRepository.findByAgendaAndProfileAndIsExistTrue(agenda,
				seoulUserAgendaProfile).orElse(null);
			assertThat(updatedAtp.getIsExist()).isTrue();
		}

		@Test
		@DisplayName("404 agendaProfile 없음으로 인한 실패")
		public void noAgendaProfileFail() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(SEOUL);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, SEOUL);
			ticketFixture.createTicket(seoulUserAgendaProfile);
			User noProfileUser = testDataUtils.createNewUser();
			String noProfileUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(noProfileUser);
			// when && then
			mockMvc.perform(
					post("/agenda/team/join")
						.header("Authorization", "Bearer " + noProfileUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("404 agenda 없음으로 인한 실패")
		public void noAgendaFail() throws Exception {
			//given
			UUID noAgendaKey = UUID.randomUUID();
			UUID noTeamKey = UUID.randomUUID();
			// when && then
			mockMvc.perform(
					post("/agenda/team/join")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", noAgendaKey.toString())
						.param("teamKey", noTeamKey.toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("404 team 없음으로 인한 실패")
		public void noTeamFail() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(SEOUL);
			ticketFixture.createTicket(seoulUserAgendaProfile);
			// when && then
			mockMvc.perform(
					post("/agenda/team/join")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", UUID.randomUUID().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("400 참가 불가능한 지역으로 인한 실패")
		public void notValidAgendaLocation() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(SEOUL);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, SEOUL);
			ticketFixture.createTicket(gyeongsanUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team/join")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("400 참가 불가능한 인원으로 인한 실패")
		public void notValidAgendaTeam() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(SEOUL);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda.getMaxPeople(), agenda, seoulUser, SEOUL);
			ticketFixture.createTicket(seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team/join")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("400 참가 불가능한 Agenda 시간으로 인한 실패")
		public void notValidAgendaStatus() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(LocalDateTime.now().minusHours(50));
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, SEOUL);
			ticketFixture.createTicket(seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team/join")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("400 참가 불가능한 Agenda status 으로 인한 실패")
		public void notValidAgendaDeadline() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(FINISH);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, SEOUL);
			ticketFixture.createTicket(seoulUserAgendaProfile);
			TeamKeyReqDto req = new TeamKeyReqDto(team.getTeamKey());
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					post("/agenda/team/join")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("409 이미 같은 아젠다에 참가 신청으로 인한 실패")
		public void alreadyApplyFail() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(SEOUL);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, SEOUL);
			ticketFixture.createTicket(seoulUserAgendaProfile);
			agendaTeamProfileFixture.createAgendaTeamProfile(agenda, team, seoulUserAgendaProfile);
			// when && then
			mockMvc.perform(
					post("/agenda/team/join")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("403 티켓 없음으로 인한 실패")
		public void noTicketFail() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(SEOUL);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, SEOUL);
			// when && then
			mockMvc.perform(
					post("/agenda/team/join")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("404 참가 불가능한 Team Status Cancel로 인한 실패")
		public void notValidTeamStatus() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(SEOUL);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, SEOUL, AgendaTeamStatus.CANCEL);
			ticketFixture.createTicket(seoulUserAgendaProfile);
			// when && then
			mockMvc.perform(
					post("/agenda/team/join")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("400 참가 불가능한 Team Status Confirm으로 인한 실패")
		public void notValidTeamStatusConfirm() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(SEOUL);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, SEOUL, AgendaTeamStatus.CONFIRM);
			ticketFixture.createTicket(seoulUserAgendaProfile);
			// when && then
			mockMvc.perform(
					post("/agenda/team/join")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.param("teamKey", team.getTeamKey().toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("팀 수정 테스트")
	class UpdateTeamTest {
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
		@DisplayName("204 팀장 팀 수정 성공")
		public void updateTeamSuccess() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(MIX);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, seoulUser, SEOUL);
			AgendaTeamProfile atp = agendaTeamProfileFixture.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamUpdateReqDto req = new TeamUpdateReqDto(team.getTeamKey(), "newName", "newDesc", true, "MIX");
			String content = objectMapper.writeValueAsString(req);
			// when
			mockMvc.perform(
					patch("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
			// then
			AgendaTeam updatedTeam = agendaTeamRepository.findByTeamKey(team.getTeamKey()).orElse(null);
			assert updatedTeam != null;
			assertThat(updatedTeam.getName()).isEqualTo("newName");
			assertThat(updatedTeam.getContent()).isEqualTo("newDesc");
			assertThat(updatedTeam.getIsPrivate()).isTrue();
			assertThat(updatedTeam.getLocation()).isEqualTo(MIX);
		}

		@Test
		@DisplayName("404 agenda 없음으로 인한 실패")
		public void noAgendaFail() throws Exception {
			//given
			UUID noAgendaKey = UUID.randomUUID();
			TeamUpdateReqDto req = new TeamUpdateReqDto(UUID.randomUUID(), "newName", "newDesc", true, "MIX");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", noAgendaKey.toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("404 team 없음으로 인한 실패")
		public void noTeamFail() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(MIX);
			TeamUpdateReqDto req = new TeamUpdateReqDto(UUID.randomUUID(), "newName", "newDesc", true, "MIX");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("403 팀원 팀 수정 실패")
		public void notTeamLeaderFail() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(MIX);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, seoulUser, MIX);
			agendaTeamProfileFixture.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			agendaTeamProfileFixture.createAgendaTeamProfile(team, gyeongsanUserAgendaProfile);
			TeamUpdateReqDto req = new TeamUpdateReqDto(team.getTeamKey(), "newName", "newDesc", true, "SEOUL");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team")
						.header("Authorization", "Bearer " + gyeongsanUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("400 수정 불가능한 지역으로 인한 실패")
		public void notValidAgendaLocation() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(MIX);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, seoulUser, SEOUL);
			agendaTeamProfileFixture.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamUpdateReqDto req = new TeamUpdateReqDto(team.getTeamKey(), "newName", "newDesc", true, "GYEONGSAN");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("400 수정 불가능한 인원으로 인한 실패")
		public void notValidAgendaTeam() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(MIX);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda.getMaxPeople(), agenda, seoulUser, MIX);
			agendaTeamProfileFixture.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			agendaTeamProfileFixture.createAgendaTeamProfile(team, gyeongsanUserAgendaProfile);
			TeamUpdateReqDto req = new TeamUpdateReqDto(team.getTeamKey(), "newName", "newDesc", true, "SEOUL");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("400 수정 불가능한 Agenda 시간으로 인한 실패")
		public void notValidAgendaStatus() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(LocalDateTime.now().minusHours(50));
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, seoulUser, MIX);
			agendaTeamProfileFixture.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamUpdateReqDto req = new TeamUpdateReqDto(team.getTeamKey(), "newName", "newDesc", true, "MIX");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("400 수정 불가능한 Agenda status 으로 인한 실패")
		public void notValidAgendaDeadline() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(FINISH);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, seoulUser, MIX);
			agendaTeamProfileFixture.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamUpdateReqDto req = new TeamUpdateReqDto(team.getTeamKey(), "newName", "newDesc", true, "MIX");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("404 수정 불가능한 Team Status Cancel로 인한 실패")
		public void notValidTeamStatus() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(MIX);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, seoulUser, MIX, AgendaTeamStatus.CANCEL);
			agendaTeamProfileFixture.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamUpdateReqDto req = new TeamUpdateReqDto(team.getTeamKey(), "newName", "newDesc", true, "MIX");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("400 수정 불가능한 Team Status Confirm으로 인한 실패")
		public void notValidTeamStatusConfirm() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(MIX);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, seoulUser, MIX, AgendaTeamStatus.CONFIRM);
			agendaTeamProfileFixture.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamUpdateReqDto req = new TeamUpdateReqDto(team.getTeamKey(), "newName", "newDesc", true, "MIX");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("400 수정 불가능한 Team Status Cancel로 인한 실패")
		public void notValidTeamStatusCancel() throws Exception {
			//given
			Agenda agenda = agendaFixture.createAgenda(MIX);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, seoulUser, MIX, AgendaTeamStatus.CANCEL);
			agendaTeamProfileFixture.createAgendaTeamProfile(team, seoulUserAgendaProfile);
			TeamUpdateReqDto req = new TeamUpdateReqDto(team.getTeamKey(), "newName", "newDesc", true, "MIX");
			String content = objectMapper.writeValueAsString(req);
			// when && then
			mockMvc.perform(
					patch("/agenda/team")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("agenda_key", agenda.getAgendaKey().toString())
						.content(content)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}
	}
}
