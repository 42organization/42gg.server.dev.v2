package gg.agenda.api.admin.agendateam.controller;

import static gg.data.agenda.type.Location.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaTeamAdminRepository;
import gg.admin.repo.agenda.AgendaTeamProfileAdminRepository;
import gg.agenda.api.admin.agendateam.controller.request.AgendaTeamMateReqDto;
import gg.agenda.api.admin.agendateam.controller.request.AgendaTeamUpdateDto;
import gg.agenda.api.admin.agendateam.controller.response.AgendaTeamDetailResDto;
import gg.agenda.api.admin.agendateam.controller.response.AgendaTeamMateResDto;
import gg.agenda.api.admin.agendateam.controller.response.AgendaTeamResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.utils.AgendaTestDataUtils;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.PageResponseDto;
import gg.utils.fixture.agenda.AgendaFixture;
import gg.utils.fixture.agenda.AgendaProfileFixture;
import gg.utils.fixture.agenda.AgendaTeamFixture;
import gg.utils.fixture.agenda.AgendaTeamProfileFixture;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class AgendaTeamAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestDataUtils testDataUtils;

	@Autowired
	private AgendaFixture agendaFixture;

	@Autowired
	private AgendaTeamFixture agendaTeamFixture;

	@Autowired
	private AgendaProfileFixture agendaProfileFixture;

	@Autowired
	private AgendaTeamProfileFixture agendaTeamProfileFixture;

	@Autowired
	private AgendaTestDataUtils agendaTestDataUtils;

	@Autowired
	EntityManager em;

	@Autowired
	AgendaAdminRepository agendaAdminRepository;

	@Autowired
	AgendaTeamAdminRepository agendaTeamAdminRepository;

	@Autowired
	AgendaTeamProfileAdminRepository agendaTeamProfileAdminRepository;

	private User user;

	private String accessToken;

	@BeforeEach
	void setUp() {
		user = testDataUtils.createAdminUser();
		accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
	}

	@Nested
	@DisplayName("Admin AgendaTeam 전체 조회")
	class GetAgendaTeamListAdmin {

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3, 4, 5})
		@DisplayName("Admin AgendaTeam 전체 조회 성공")
		void getAgendaTeamListAdminSuccess(int page) throws Exception {
			// given
			int size = 10;
			int total = 37;
			Agenda agenda = agendaFixture.createAgenda(2, 50, 1, 10);
			List<AgendaTeam> teams = agendaTeamFixture
				.createAgendaTeamList(agenda, AgendaTeamStatus.CONFIRM, total);

			// when
			String response = mockMvc.perform(get("/agenda/admin/team/list")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.param("page", String.valueOf(page))
					.param("size", String.valueOf(size)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			PageResponseDto<AgendaTeamResDto> pageResponseDto = objectMapper
				.readValue(response, new TypeReference<>() {
				});
			List<AgendaTeamResDto> result = pageResponseDto.getContent();

			// then
			assertThat(result).isNotNull();
			assertThat(result.size()).isEqualTo(((page - 1) * size) < teams.size()
				? Math.min(size, teams.size() - (page - 1) * size) : 0);
			teams.sort((a, b) -> b.getId().compareTo(a.getId()));
			for (int i = 0; i < result.size(); i++) {
				assertThat(result.get(i).getTeamKey()).isEqualTo(teams.get(i + (page - 1) * size).getTeamKey());
			}
		}

		@Test
		@DisplayName("Admin AgendaTeam 전체 조회 실패 - Agenda 없음")
		void getAgendaTeamListAdminFailedWithNoAgenda() throws Exception {
			// given
			int page = 1;
			int size = 10;

			// expected
			mockMvc.perform(get("/agenda/admin/team/list")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", UUID.randomUUID().toString())
					.param("page", String.valueOf(page))
					.param("size", String.valueOf(size)))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("Admin AgendaTeam 상세 조회")
	class GetAgendaTeamDetailAdmin {
		@Test
		@DisplayName("Admin AgendaTeam 상세 조회 성공")
		void getAgendaTeamDetailAdminSuccess() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda);
			List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(5);
			profiles.forEach(profile -> agendaTeamProfileFixture
				.createAgendaTeamProfile(agenda, team, profile));

			// when
			String response = mockMvc.perform(get("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.param("team_key", team.getTeamKey().toString()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaTeamDetailResDto result = objectMapper.readValue(response, AgendaTeamDetailResDto.class);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getTeamName()).isEqualTo(team.getName());
			assertThat(result.getTeamMates()).isNotNull();
			assertThat(result.getTeamMates().size()).isEqualTo(profiles.size());
			for (int i = 0; i < profiles.size(); i++) {
				AgendaTeamMateResDto profile = result.getTeamMates().get(i);
				assertThat(profile.getIntraId()).isEqualTo(profiles.get(i).getIntraId());
			}
		}

		@Test
		@DisplayName("Admin AgendaTeam 상세 조회 실패 - Team Key 없음")
		void getAgendaTeamDetailAdminFailedWithNoTeamKey() throws Exception {
			// given
			// expected
			mockMvc.perform(get("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin AgendaTeam 상세 조회 실패 - 존재하지 않는 Team")
		void getAgendaTeamDetailAdminFailedWithNotFoundTeam() throws Exception {
			// given
			// expected
			mockMvc.perform(get("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.param("team_key", UUID.randomUUID().toString()))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("Admin AgendaTeam 상세 조회 성공 - teamMate 없는 경우 빈 리스트")
		void getAgendaTeamDetailAdminSuccessWithNoTeamMates() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda);

			// when
			String response = mockMvc.perform(get("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.param("team_key", team.getTeamKey().toString()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaTeamDetailResDto result = objectMapper.readValue(response, AgendaTeamDetailResDto.class);

			// then
			assertThat(result).isNotNull();
			assertThat(result.getTeamName()).isEqualTo(team.getName());
			assertThat(result.getTeamMates()).isNotNull();
			assertThat(result.getTeamMates().size()).isEqualTo(0);
		}
	}

	@Nested
	@DisplayName("Admin AgendaTeam 수정")
	class UpdateAgendaTeamAdmin {
		@Test
		@DisplayName("Admin AgendaTeam 수정 성공")
		void updateAgendaTeamAdminSuccess() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaProfile seoulUserAgendaProfile = agendaProfileFixture.createAgendaProfile();
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, seoulUserAgendaProfile);
			List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(5);
			profiles.forEach(profile -> agendaTeamProfileFixture
				.createAgendaTeamProfile(agenda, team, profile));
			agendaTeamProfileFixture.createAgendaTeamProfile(team, seoulUserAgendaProfile);

			List<AgendaTeamMateReqDto> updateTeamMates = profiles.stream()
				.map(profile -> new AgendaTeamMateReqDto(profile.getIntraId()))
				.collect(Collectors.toList());
			updateTeamMates.add(new AgendaTeamMateReqDto(seoulUserAgendaProfile.getIntraId()));
			AgendaTeamUpdateDto updateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(updateTeamMates)
				.teamStatus(AgendaTeamStatus.CONFIRM).teamLocation(Location.MIX)
				.teamName("newName").teamContent("newContent").teamIsPrivate(true)
				.teamAward("newAward").teamAwardPriority(team.getAwardPriority() + 1).build();
			String request = objectMapper.writeValueAsString(updateDto);

			// when
			mockMvc.perform(patch("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNoContent());
			AgendaTeam updatedAgendaTeam = agendaTeamAdminRepository.findByTeamKey(team.getTeamKey())
				.orElseThrow(() -> new AssertionError("AgendaTeam not found"));

			// then
			assertThat(updatedAgendaTeam.getName()).isEqualTo(updateDto.getTeamName());
			assertThat(updatedAgendaTeam.getContent()).isEqualTo(updateDto.getTeamContent());
			assertThat(updatedAgendaTeam.getIsPrivate()).isEqualTo(updateDto.getTeamIsPrivate());
			assertThat(updatedAgendaTeam.getStatus()).isEqualTo(updateDto.getTeamStatus());
			assertThat(updatedAgendaTeam.getAward()).isEqualTo(updateDto.getTeamAward());
			assertThat(updatedAgendaTeam.getAwardPriority()).isEqualTo(updateDto.getTeamAwardPriority());
			assertThat(updatedAgendaTeam.getLocation()).isEqualTo(updateDto.getTeamLocation());
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 실패 - Location을 변경할 수 없는 경우")
		void updateAgendaTeamAdminFailedWithLocation() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda(Location.MIX);
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda);
			List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(5);
			profiles.forEach(profile -> agendaTeamProfileFixture
				.createAgendaTeamProfile(agenda, team, profile));

			List<AgendaTeamMateReqDto> updateTeamMates = profiles.stream()
				.map(profile -> new AgendaTeamMateReqDto(profile.getIntraId()))
				.collect(Collectors.toList());
			AgendaTeamUpdateDto updateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(updateTeamMates)
				.teamStatus(AgendaTeamStatus.CONFIRM).teamLocation(Location.GYEONGSAN)
				.teamName("newName").teamContent("newContent").teamIsPrivate(true)
				.teamAward("newAward").teamAwardPriority(team.getAwardPriority() + 1).build();
			String request = objectMapper.writeValueAsString(updateDto);

			// when
			mockMvc.perform(patch("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isBadRequest());
			AgendaTeam result = agendaTeamAdminRepository.findByTeamKey(team.getTeamKey())
				.orElseThrow(() -> new AssertionError("AgendaTeam not found"));

			// then
			assertThat(result.getLocation()).isEqualTo(team.getLocation());
			assertThat(result.getName()).isEqualTo(team.getName());
			assertThat(result.getContent()).isEqualTo(team.getContent());
			assertThat(result.getIsPrivate()).isEqualTo(team.getIsPrivate());
			assertThat(result.getStatus()).isEqualTo(team.getStatus());
			assertThat(result.getAward()).isEqualTo(team.getAward());
			assertThat(result.getAwardPriority()).isEqualTo(team.getAwardPriority());
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 성공 - 팀원 추가하기")
		void updateAgendaTeamAdminSuccessWithAddTeammate() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaProfile seoulUserAgendaProfile = agendaProfileFixture.createAgendaProfile();
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, seoulUserAgendaProfile);
			List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(3);
			profiles.forEach(profile -> agendaTeamProfileFixture
				.createAgendaTeamProfile(agenda, team, profile));
			AgendaProfile newProfile = agendaProfileFixture.createAgendaProfile();
			agendaTeamProfileFixture.createAgendaTeamProfile(team, seoulUserAgendaProfile);

			List<AgendaTeamMateReqDto> updateTeamMates = profiles.stream()
				.map(profile -> new AgendaTeamMateReqDto(profile.getIntraId()))
				.collect(Collectors.toList());
			updateTeamMates.add(new AgendaTeamMateReqDto(seoulUserAgendaProfile.getIntraId()));
			updateTeamMates.add(new AgendaTeamMateReqDto(newProfile.getIntraId()));
			AgendaTeamUpdateDto updateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(updateTeamMates)
				.teamStatus(AgendaTeamStatus.CONFIRM).teamLocation(Location.MIX)
				.teamName("newName").teamContent("newContent").teamIsPrivate(true)
				.teamAward("newAward").teamAwardPriority(team.getAwardPriority() + 1).build();
			String request = objectMapper.writeValueAsString(updateDto);

			// when
			mockMvc.perform(patch("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNoContent());
			AgendaTeam updatedAgendaTeam = agendaTeamAdminRepository.findByTeamKey(team.getTeamKey())
				.orElseThrow(() -> new AssertionError("AgendaTeam not found"));
			List<AgendaTeamProfile> participants = agendaTeamProfileAdminRepository
				.findAllByAgendaTeamAndIsExistIsTrue(updatedAgendaTeam);

			// then
			assertThat(updatedAgendaTeam.getName()).isEqualTo(updateDto.getTeamName());
			assertThat(updatedAgendaTeam.getContent()).isEqualTo(updateDto.getTeamContent());
			assertThat(updatedAgendaTeam.getIsPrivate()).isEqualTo(updateDto.getTeamIsPrivate());
			assertThat(updatedAgendaTeam.getStatus()).isEqualTo(updateDto.getTeamStatus());
			assertThat(updatedAgendaTeam.getAward()).isEqualTo(updateDto.getTeamAward());
			assertThat(updatedAgendaTeam.getAwardPriority()).isEqualTo(updateDto.getTeamAwardPriority());
			assertThat(updatedAgendaTeam.getLocation()).isEqualTo(updateDto.getTeamLocation());
			assertThat(participants.size()).isEqualTo(updateTeamMates.size());
			// Check new participant
			assertThat(participants.stream()
				.anyMatch(participant -> participant.getProfile().getIntraId().equals(newProfile.getIntraId())))
				.isTrue();
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 실패 - AgendaTeamStatus는 Cancel로 변경할 수 없음")
		void updateAgendaTeamAdminFailedWithCancelStatus() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda);
			List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(5);
			profiles.forEach(profile -> agendaTeamProfileFixture
				.createAgendaTeamProfile(agenda, team, profile));

			List<AgendaTeamMateReqDto> updateTeamMates = profiles.stream()
				.map(profile -> new AgendaTeamMateReqDto(profile.getIntraId()))
				.collect(Collectors.toList());
			AgendaTeamUpdateDto updateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(updateTeamMates)
				.teamStatus(AgendaTeamStatus.CANCEL).teamLocation(Location.MIX)
				.teamName("newName").teamContent("newContent").teamIsPrivate(true)
				.teamAward("newAward").teamAwardPriority(team.getAwardPriority() + 1).build();
			String request = objectMapper.writeValueAsString(updateDto);

			// when
			mockMvc.perform(patch("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 실패 - 이미 꽉 찬 팀에 팀원 추가하기")
		void updateAgendaTeamAdminFailedWithMaxPeople() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda);
			List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(5);
			profiles.forEach(profile -> agendaTeamProfileFixture
				.createAgendaTeamProfile(agenda, team, profile));
			AgendaProfile newProfile = agendaProfileFixture.createAgendaProfile();

			List<AgendaTeamMateReqDto> updateTeamMates = profiles.stream()
				.map(profile -> new AgendaTeamMateReqDto(profile.getIntraId()))
				.collect(Collectors.toList());
			updateTeamMates.add(new AgendaTeamMateReqDto(newProfile.getIntraId()));
			AgendaTeamUpdateDto updateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(updateTeamMates)
				.teamStatus(AgendaTeamStatus.CONFIRM).teamLocation(Location.MIX)
				.teamName("newName").teamContent("newContent").teamIsPrivate(true)
				.teamAward("newAward").teamAwardPriority(team.getAwardPriority() + 1).build();
			String request = objectMapper.writeValueAsString(updateDto);

			// when
			mockMvc.perform(patch("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 성공 - 팀원 삭제하기")
		void updateAgendaTeamAdminSuccessWithRemoveTeammate() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaProfile seoulUserAgendaProfile = agendaProfileFixture.createAgendaProfile();
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, seoulUserAgendaProfile);
			List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(3);
			profiles.forEach(profile -> agendaTeamProfileFixture
				.createAgendaTeamProfile(agenda, team, profile));
			AgendaProfile wrongProfile = agendaProfileFixture.createAgendaProfile();
			agendaTeamProfileFixture.createAgendaTeamProfile(agenda, team, wrongProfile);
			agendaTeamProfileFixture.createAgendaTeamProfile(team, seoulUserAgendaProfile);

			List<AgendaTeamMateReqDto> updateTeamMates = profiles.stream()
				.map(profile -> new AgendaTeamMateReqDto(profile.getIntraId()))
				.collect(Collectors.toList());
			updateTeamMates.add(new AgendaTeamMateReqDto(seoulUserAgendaProfile.getIntraId()));
			AgendaTeamUpdateDto updateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(updateTeamMates)
				.teamStatus(AgendaTeamStatus.CONFIRM).teamLocation(Location.MIX)
				.teamName("newName").teamContent("newContent").teamIsPrivate(true)
				.teamAward("newAward").teamAwardPriority(team.getAwardPriority() + 1).build();
			String request = objectMapper.writeValueAsString(updateDto);

			// when
			mockMvc.perform(patch("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNoContent());
			AgendaTeam updatedAgendaTeam = agendaTeamAdminRepository.findByTeamKey(team.getTeamKey())
				.orElseThrow(() -> new AssertionError("AgendaTeam not found"));
			List<AgendaTeamProfile> participants = agendaTeamProfileAdminRepository
				.findAllByAgendaTeamAndIsExistIsTrue(updatedAgendaTeam);

			// then
			assertThat(updatedAgendaTeam.getName()).isEqualTo(updateDto.getTeamName());
			assertThat(updatedAgendaTeam.getContent()).isEqualTo(updateDto.getTeamContent());
			assertThat(updatedAgendaTeam.getIsPrivate()).isEqualTo(updateDto.getTeamIsPrivate());
			assertThat(updatedAgendaTeam.getStatus()).isEqualTo(updateDto.getTeamStatus());
			assertThat(updatedAgendaTeam.getAward()).isEqualTo(updateDto.getTeamAward());
			assertThat(updatedAgendaTeam.getAwardPriority()).isEqualTo(updateDto.getTeamAwardPriority());
			assertThat(updatedAgendaTeam.getLocation()).isEqualTo(updateDto.getTeamLocation());
			assertThat(participants.size()).isEqualTo(updateTeamMates.size());
			// Check wrong participant
			assertThat(participants.stream()
				.noneMatch(participant -> participant.getProfile().getIntraId().equals(wrongProfile.getIntraId())))
				.isTrue();
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 실패 - 리더를 삭제하는 경우")
		void updateAgendaTeamAdminFailedWithRemoveLeader() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaProfile seoulUserAgendaProfile = agendaProfileFixture.createAgendaProfile();
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, seoulUserAgendaProfile);
			List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(3);
			profiles.forEach(profile -> agendaTeamProfileFixture
				.createAgendaTeamProfile(agenda, team, profile));

			List<AgendaTeamMateReqDto> updateTeamMates = profiles.stream()
				.map(profile -> new AgendaTeamMateReqDto(profile.getIntraId()))
				.collect(Collectors.toList());
			AgendaTeamUpdateDto updateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(updateTeamMates)
				.teamStatus(AgendaTeamStatus.CONFIRM).teamLocation(Location.MIX)
				.teamName("newName").teamContent("newContent").teamIsPrivate(true)
				.teamAward("newAward").teamAwardPriority(team.getAwardPriority() + 1).build();
			String request = objectMapper.writeValueAsString(updateDto);

			// when
			mockMvc.perform(patch("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 실패 - 팀장이 존재하지 않음")
		void updateAgendaTeamAdminFailedWithNoLeader() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda);
			List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(3);
			profiles.forEach(profile -> agendaTeamProfileFixture
				.createAgendaTeamProfile(agenda, team, profile));

			List<AgendaTeamMateReqDto> updateTeamMates = profiles.stream()
				.map(profile -> new AgendaTeamMateReqDto(profile.getIntraId()))
				.collect(Collectors.toList());
			AgendaTeamUpdateDto updateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(updateTeamMates)
				.teamStatus(AgendaTeamStatus.CONFIRM).teamLocation(Location.MIX)
				.teamName("newName").teamContent("newContent").teamIsPrivate(true)
				.teamAward("newAward").teamAwardPriority(team.getAwardPriority() + 1).build();
			String request = objectMapper.writeValueAsString(updateDto);

			// when
			mockMvc.perform(patch("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 실패 - 존재하지 않는 Team Key")
		void updateAgendaTeamAdminFailedWithInvalidTeamKey() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda);
			List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(5);
			profiles.forEach(profile -> agendaTeamProfileFixture
				.createAgendaTeamProfile(agenda, team, profile));

			List<AgendaTeamMateReqDto> updateTeamMates = profiles.stream()
				.map(profile -> new AgendaTeamMateReqDto(profile.getIntraId()))
				.collect(Collectors.toList());
			AgendaTeamUpdateDto updateDto = AgendaTeamUpdateDto.builder()
				.teamKey(UUID.randomUUID()).teamMates(updateTeamMates)
				.teamStatus(AgendaTeamStatus.CONFIRM).teamLocation(Location.MIX)
				.teamName("newName").teamContent("newContent").teamIsPrivate(true)
				.teamAward("newAward").teamAwardPriority(team.getAwardPriority() + 1).build();
			String request = objectMapper.writeValueAsString(updateDto);

			// when
			mockMvc.perform(patch("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("Admin AgendaTeam 수정 실패 - 존재하지 않는 Intra ID")
		void updateAgendaTeamAdminFailedWithInvalidIntraId() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda);
			List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(5);
			profiles.forEach(profile -> agendaTeamProfileFixture
				.createAgendaTeamProfile(agenda, team, profile));

			List<AgendaTeamMateReqDto> updateTeamMates = profiles.stream()
				.map(profile -> new AgendaTeamMateReqDto(profile.getIntraId()))
				.collect(Collectors.toList());
			updateTeamMates.add(new AgendaTeamMateReqDto("invalid"));
			AgendaTeamUpdateDto updateDto = AgendaTeamUpdateDto.builder()
				.teamKey(team.getTeamKey()).teamMates(updateTeamMates)
				.teamStatus(AgendaTeamStatus.CONFIRM).teamLocation(Location.MIX)
				.teamName("newName").teamContent("newContent").teamIsPrivate(true)
				.teamAward("newAward").teamAwardPriority(team.getAwardPriority() + 1).build();
			String request = objectMapper.writeValueAsString(updateDto);

			// when
			mockMvc.perform(patch("/agenda/admin/team")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNotFound());
			AgendaTeam result = agendaTeamAdminRepository.findByTeamKey(team.getTeamKey())
				.orElseThrow(() -> new AssertionError("AgendaTeam not found"));

			// then
			assertThat(result.getLocation()).isEqualTo(team.getLocation());
			assertThat(result.getName()).isEqualTo(team.getName());
			assertThat(result.getContent()).isEqualTo(team.getContent());
			assertThat(result.getIsPrivate()).isEqualTo(team.getIsPrivate());
			assertThat(result.getStatus()).isEqualTo(team.getStatus());
			assertThat(result.getAward()).isEqualTo(team.getAward());
			assertThat(result.getAwardPriority()).isEqualTo(team.getAwardPriority());
		}
	}

	@Nested
	@DisplayName("Admin AgendaTeam 취소")
	class CancelAgendaTeamAdmin {
		@Test
		@DisplayName("Admin AgendaTeam 취소 성공")
		void cancelAgendaTeamAdminSuccess() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda);
			List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(5);
			profiles.forEach(profile -> agendaTeamProfileFixture
				.createAgendaTeamProfile(agenda, team, profile));

			// when
			mockMvc.perform(patch("/agenda/admin/team/cancel")
					.header("Authorization", "Bearer " + accessToken)
					.param("team_key", team.getTeamKey().toString()))
				.andExpect(status().isNoContent());
			AgendaTeam result = agendaTeamAdminRepository.findByTeamKey(team.getTeamKey())
				.orElseThrow(() -> new AssertionError("AgendaTeam not found"));

			// then
			assertThat(result.getStatus()).isEqualTo(AgendaTeamStatus.CANCEL);
			assertThat(agendaTeamProfileAdminRepository
				.findAllByAgendaTeamAndIsExistIsTrue(result).size()).isEqualTo(0);
			assertThat(agenda.getCurrentTeam()).isEqualTo(1);
		}

		@Nested
		@DisplayName("Admin Confirm 상태의 AgendaTeam 취소")
		class CancelConfirmAgendaTeamAdmin {
			@Test
			@DisplayName("Admin AgendaTeam 취소 성공")
			void cancelAgendaTeamAdminSuccess() throws Exception {
				// given
				Agenda agenda = agendaFixture.createAgenda();
				AgendaTeam team = agendaTeamFixture.createAgendaTeam(agenda, SEOUL, AgendaTeamStatus.CONFIRM);
				List<AgendaProfile> profiles = agendaProfileFixture.createAgendaProfileList(5);
				profiles.forEach(profile -> agendaTeamProfileFixture
					.createAgendaTeamProfile(team, profile));

				// when
				mockMvc.perform(patch("/agenda/admin/team/cancel")
						.header("Authorization", "Bearer " + accessToken)
						.param("team_key", team.getTeamKey().toString()))
					.andExpect(status().isNoContent());
				AgendaTeam result = agendaTeamAdminRepository.findByTeamKey(team.getTeamKey())
					.orElseThrow(() -> new AssertionError("AgendaTeam not found"));

				// then
				assertThat(result.getStatus()).isEqualTo(AgendaTeamStatus.CANCEL);
				assertThat(agendaTeamProfileAdminRepository
					.findAllByAgendaTeamAndIsExistIsTrue(result).size()).isEqualTo(0);
				assertThat(agenda.getCurrentTeam()).isEqualTo(0);
			}
		}

		@Test
		@DisplayName("Admin AgendaTeam 취소 실패 - 존재하지 않는 Team Key")
		void cancelAgendaTeamAdminFailedWithInvalidTeamKey() throws Exception {
			// given
			// expected
			mockMvc.perform(patch("/agenda/admin/team/cancel")
					.header("Authorization", "Bearer " + accessToken)
					.param("team_key", UUID.randomUUID().toString()))
				.andExpect(status().isNotFound());
		}
	}
}
