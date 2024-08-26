package gg.agenda.api.user.agendaprofile;

import static gg.data.agenda.type.Location.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gg.agenda.api.AgendaMockData;
import gg.agenda.api.user.agendaprofile.controller.request.AgendaProfileChangeReqDto;
import gg.agenda.api.user.agendaprofile.controller.response.AgendaProfileDetailsResDto;
import gg.agenda.api.user.agendaprofile.controller.response.AgendaProfileInfoDetailsResDto;
import gg.agenda.api.user.agendaprofile.controller.response.AttendedAgendaListResDto;
import gg.agenda.api.user.agendaprofile.controller.response.CurrentAttendAgendaListResDto;
import gg.agenda.api.user.agendaprofile.controller.response.MyAgendaProfileDetailsResDto;
import gg.agenda.api.user.agendaprofile.service.IntraProfileUtils;
import gg.agenda.api.user.agendaprofile.service.intraprofile.IntraProfile;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.data.user.type.RoleType;
import gg.repo.agenda.AgendaProfileRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;

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
	@Autowired
	private AgendaProfileRepository agendaProfileRepository;

	@MockBean
	private IntraProfileUtils intraProfileUtils;

	User user;
	String accessToken;
	AgendaProfile agendaProfile;

	@Nested
	@DisplayName("나의 agenda profile 상세 조회")
	class GetMyAgendaProfile {

		@BeforeEach
		void beforeEach() {
			user = testDataUtils.createNewUser();
			accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		}

		@Test
		@DisplayName("로그인된 유저에 해당하는 Agenda profile를 상세 조회합니다.")
		void test() throws Exception {
			//given
			URL url = new URL("http://localhost:8080");
			IntraProfile intraProfile = new IntraProfile(url, List.of());
			Mockito.when(intraProfileUtils.getIntraProfile()).thenReturn(intraProfile);
			AgendaProfile agendaProfile = agendaMockData.createAgendaProfile(user, SEOUL);
			agendaMockData.createTicket(agendaProfile);
			// when
			String response = mockMvc.perform(get("/agenda/profile")
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			MyAgendaProfileDetailsResDto result = objectMapper.readValue(response, MyAgendaProfileDetailsResDto.class);
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

	@Nested
	@DisplayName("agenda profile 상세 조회")
	class GetAgendaProfile {

		@BeforeEach
		void beforeEach() {
			user = testDataUtils.createNewUser();
			accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		}

		@Test
		@DisplayName("agenda profile 상세 조회 성공")
		void getAgendaProfileSuccess() throws Exception {
			//given
			URL url = new URL("http://localhost:8080");
			IntraProfile intraProfile = new IntraProfile(url, List.of());
			Mockito.when(intraProfileUtils.getIntraProfile(user.getIntraId())).thenReturn(intraProfile);
			AgendaProfile agendaProfile = agendaMockData.createAgendaProfile(user, SEOUL);
			agendaMockData.createTicket(agendaProfile);
			// when
			String response = mockMvc.perform(get("/agenda/profile/" + user.getIntraId())
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			AgendaProfileDetailsResDto result = objectMapper.readValue(response,
				AgendaProfileDetailsResDto.class);

			// then
			assertThat(result.getUserIntraId()).isEqualTo(user.getIntraId());
			assertThat(result.getUserContent()).isEqualTo(agendaProfile.getContent());
			assertThat(result.getUserGithub()).isEqualTo(agendaProfile.getGithubUrl());
			assertThat(result.getUserCoalition()).isEqualTo(agendaProfile.getCoalition());
			assertThat(result.getUserLocation()).isEqualTo(agendaProfile.getLocation());
		}

		@Test
		@DisplayName("agenda profile 상세 조회 성공 - 존재하지 않는 사용자인 경우")
		void getAgendaProfileFailedWithInvalidIntraId() throws Exception {
			//given
			URL url = new URL("http://localhost:8080");
			IntraProfile intraProfile = new IntraProfile(url, List.of());
			Mockito.when(intraProfileUtils.getIntraProfile()).thenReturn(intraProfile);
			AgendaProfile agendaProfile = agendaMockData.createAgendaProfile(user, SEOUL);
			agendaMockData.createTicket(agendaProfile);

			// when
			mockMvc.perform(get("/agenda/profile/" + "invalidIntraId")
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("개인 프로필 정보 변경")
	class UpdateAgendaProfile {
		@BeforeEach
		void beforeEach() {
			user = testDataUtils.createNewUser();
			accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		}

		@Test
		@DisplayName("유효한 정보로 개인 프로필을 변경합니다.")
		void updateProfileWithValidData() throws Exception {
			// Given
			AgendaProfile agendaProfile = agendaMockData.createAgendaProfile(user, SEOUL);
			agendaMockData.createTicket(agendaProfile);
			AgendaProfileChangeReqDto requestDto = new AgendaProfileChangeReqDto("Valid user content",
				"https://github.com/validUser");
			String content = objectMapper.writeValueAsString(requestDto);
			// When
			mockMvc.perform(patch("/agenda/profile")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isNoContent());
			// Then
			AgendaProfile result = agendaProfileRepository.findByUserId(user.getId()).orElseThrow(null);
			assertThat(result.getContent()).isEqualTo(requestDto.getUserContent());
			assertThat(result.getGithubUrl()).isEqualTo(requestDto.getUserGithub());
		}

		@Test
		@DisplayName("userContent 없이 개인 프로필을 변경합니다.")
		void updateProfileWithoutUserContent() throws Exception {
			// Given
			AgendaProfileChangeReqDto requestDto = new AgendaProfileChangeReqDto("", "https://github.com/validUser");
			String content = objectMapper.writeValueAsString(requestDto);
			// When & Then
			mockMvc.perform(patch("/agenda/profile")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("잘못된 형식의 userGithub로 개인 프로필을 변경합니다.")
		void updateProfileWithInvalidUserGithub() throws Exception {
			// Given
			AgendaProfileChangeReqDto requestDto = new AgendaProfileChangeReqDto("Valid user content",
				"invalidGithubUrl");
			String content = objectMapper.writeValueAsString(requestDto);
			// When & Then
			mockMvc.perform(patch("/agenda/profile")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("userContent가 허용된 길이를 초과하여 개인 프로필을 변경합니다.")
		void updateProfileWithExceededUserContentLength() throws Exception {
			// Given
			String longContent = "a".repeat(1001); // Assuming the limit is 1000 characters
			AgendaProfileChangeReqDto requestDto = new AgendaProfileChangeReqDto(longContent,
				"https://github.com/validUser");
			String content = objectMapper.writeValueAsString(requestDto);
			// When & Then
			mockMvc.perform(patch("/agenda/profile")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("userGithub가 허용된 길이를 초과하여 개인 프로필을 변경합니다.")
		void updateProfileWithExceededUserGithubLength() throws Exception {
			// Given
			String longGithubUrl = "https://github.com/" + "a".repeat(256); // Assuming the limit is 255 characters
			AgendaProfileChangeReqDto requestDto = new AgendaProfileChangeReqDto("Valid user content", longGithubUrl);

			String content = objectMapper.writeValueAsString(requestDto);

			// When & Then
			mockMvc.perform(patch("/agenda/profile")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isBadRequest());
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

	@Nested
	@DisplayName("개인 프로필 admin 여부 조회")
	class GetAgendaProfileInfo {

		@BeforeEach
		void beforeEach() {
			user = testDataUtils.createNewUser();
			accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		}

		@Test
		@DisplayName("로그인된 유저에 해당하는 Admin 여부를 조회합니다.")
		void test() throws Exception {
			//given
			// when
			String response = mockMvc.perform(get("/agenda/profile/info")
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			AgendaProfileInfoDetailsResDto result = objectMapper.readValue(response,
				AgendaProfileInfoDetailsResDto.class);
			// then
			Boolean isAdmin = user.getRoleType() == RoleType.ADMIN;
			assertThat(result.getIntraId()).isEqualTo(user.getIntraId());
			assertThat(result.getIsAdmin()).isEqualTo(isAdmin);
		}
	}

	@Nested
	@DisplayName("내가 참여 중인 대회 보기")
	class GetCurrentAttendAgendaList {
		@BeforeEach
		void beforeEach() {
			user = testDataUtils.createNewUser();
			accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		}

		@Test
		@DisplayName("200 내가 참여 중인 대회 조회 성공")
		public void getCurrentAttendAgendaListSuccess() throws Exception {
			//given
			agendaProfile = agendaMockData.createAgendaProfile(user, SEOUL);
			List<AgendaTeam> agendaTeamList = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				User agendaCreateUser = testDataUtils.createNewUser();
				User otherUser = testDataUtils.createNewUser();
				LocalDateTime startTime = LocalDateTime.now().plusDays(i);
				Agenda agenda = agendaMockData.createAgenda(agendaCreateUser.getIntraId(), startTime,
					i % 2 == 0 ? AgendaStatus.OPEN : AgendaStatus.CONFIRM);
				AgendaTeam agendaTeam = agendaMockData.createAgendaTeam(agenda, otherUser, Location.SEOUL);
				agendaMockData.createAgendaTeamProfile(agendaTeam, agendaProfile);
				agendaTeamList.add(agendaTeam);
			}

			// when
			String res = mockMvc.perform(
					get("/agenda/profile/current/list")
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

			CurrentAttendAgendaListResDto[] result = objectMapper.readValue(res, CurrentAttendAgendaListResDto[].class);

			// then
			assertThat(result.length).isEqualTo(agendaTeamList.size());
			for (int i = 0; i < result.length; i++) {
				assertThat(result[i].getAgendaId()).isEqualTo(agendaTeamList.get(i).getAgenda().getId().toString());
				assertThat(result[i].getAgendaKey()).isEqualTo(agendaTeamList.get(i).getAgenda().getAgendaKey());
				assertThat(result[i].getAgendaTitle()).isEqualTo(agendaTeamList.get(i).getAgenda().getTitle());
				assertThat(result[i].getAgendaLocation()).isEqualTo(
					agendaTeamList.get(i).getAgenda().getLocation().toString());
				assertThat(result[i].getTeamKey()).isEqualTo(agendaTeamList.get(i).getTeamKey());
				assertThat(result[i].getIsOfficial()).isEqualTo(agendaTeamList.get(i).getAgenda().getIsOfficial());
				assertThat(result[i].getTeamName()).isEqualTo(agendaTeamList.get(i).getName());
			}
		}

		@Test
		@DisplayName("200 내가 참여 중인 대회가 없을 때 조회 성공")
		public void getCurrentAttendAgendaListSuccessNoAgenda() throws Exception {
			//given
			agendaProfile = agendaMockData.createAgendaProfile(user, SEOUL);
			// 참여 중인 대회가 없는 상태

			// when
			String res = mockMvc.perform(
					get("/agenda/profile/current/list")
						.header("Authorization", "Bearer " + accessToken)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

			CurrentAttendAgendaListResDto[] result = objectMapper.readValue(res, CurrentAttendAgendaListResDto[].class);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("해당 로그인 유저의 아젠다 프로필이 없을 때")
		void testAgendaProfileNotFound() throws Exception {
			// given: 특정 유저와 관련된 AgendaProfile이 없음

			// when & then: 예외가 발생해야 함
			mockMvc.perform(get("/agenda/profile/current/list")
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("해당 로그인 유저의 AgendaTeam이 없을 때")
		void testAgendaTeamNotFound() throws Exception {
			// given: 특정 유저와 관련된 AgendaTeam이 없음

			// when & then: 예외가 발생해야 함
			mockMvc.perform(get("/agenda/profile/current/list")
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("내가 과거에 참여했던 대회 보기")
	class GetAttendedAgendaList {
		@BeforeEach
		void beforeEach() {
			user = testDataUtils.createNewUser();
			accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		}

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3})
		@DisplayName("200 내가 참여 했었던 대회 조회 성공")
		void getAttendedAgendaListSuccess(int page) throws Exception {
			// given
			int size = 10;
			int total = 25;
			agendaProfile = agendaMockData.createAgendaProfile(user, Location.SEOUL);
			List<AgendaTeamProfile> attendedAgendas = new ArrayList<>();
			for (int i = 0; i < total; i++) {
				User agendaCreateUser = testDataUtils.createNewUser();
				User otherUser = testDataUtils.createNewUser();
				LocalDateTime startTime = LocalDateTime.now().minusDays(i + 1);
				Agenda agenda = agendaMockData.createAgenda(agendaCreateUser.getIntraId(), startTime,
					AgendaStatus.FINISH);
				AgendaTeam agendaTeam = agendaMockData.createAgendaTeam(agenda, otherUser, Location.SEOUL);
				attendedAgendas.add(agendaMockData.createAgendaTeamProfile(agendaTeam, agendaProfile));
			}

			PageRequestDto pageRequest = new PageRequestDto(page, size);
			String request = objectMapper.writeValueAsString(pageRequest);

			// when
			String response = mockMvc.perform(get("/agenda/profile/history/list/" + user.getIntraId())
					.header("Authorization", "Bearer " + accessToken)
					.param("page", String.valueOf(page))
					.param("size", String.valueOf(size))
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

			PageResponseDto<AttendedAgendaListResDto> pageResponseDto = objectMapper
				.readValue(response, new TypeReference<>() {
				});
			List<AttendedAgendaListResDto> result = pageResponseDto.getContent();

			// then
			assertThat(result).hasSize(size * page < total ? size : total % size);
			attendedAgendas.sort((o1, o2) -> Long.compare(o2.getAgenda().getId(), o1.getAgenda().getId()));
			for (int i = 0; i < result.size(); i++) {
				assertThat(result.get(i).getAgendaId()).isEqualTo(
					attendedAgendas.get(i + (page - 1) * size).getAgenda().getId().toString());
				assertThat(result.get(i).getAgendaKey()).isEqualTo(
					attendedAgendas.get(i + (page - 1) * size).getAgenda().getAgendaKey());
				assertThat(result.get(i).getAgendaTitle()).isEqualTo(
					attendedAgendas.get(i + (page - 1) * size).getAgenda().getTitle());
				assertThat(result.get(i).getAgendaLocation()).isEqualTo(
					attendedAgendas.get(i + (page - 1) * size).getAgenda().getLocation().toString());
				assertThat(result.get(i).getTeamKey()).isEqualTo(
					attendedAgendas.get(i + (page - 1) * size).getAgendaTeam().getTeamKey());
				assertThat(result.get(i).getIsOfficial()).isEqualTo(
					attendedAgendas.get(i + (page - 1) * size).getAgenda().getIsOfficial());
				assertThat(result.get(i).getTeamName()).isEqualTo(
					attendedAgendas.get(i + (page - 1) * size).getAgendaTeam().getName());
			}
		}

		@Test
		@DisplayName("200 내가 참여 했었던 대회가 없을 때 조회 성공")
		void getAttendedAgendaListSuccessNoAgenda() throws Exception {
			// given
			agendaProfile = agendaMockData.createAgendaProfile(user, Location.SEOUL);
			PageRequestDto pageRequest = new PageRequestDto(1, 10);
			String request = objectMapper.writeValueAsString(pageRequest);

			// when
			String response = mockMvc.perform(get("/agenda/profile/history/list/" + user.getIntraId())
					.header("Authorization", "Bearer " + accessToken)
					.param("page", "1")
					.param("size", "10")
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

			PageResponseDto<AttendedAgendaListResDto> pageResponseDto = objectMapper
				.readValue(response, new TypeReference<>() {
				});
			List<AttendedAgendaListResDto> result = pageResponseDto.getContent();

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("400 잘못된 페이지 요청 시 실패")
		void getAttendedAgendaListBadRequest() throws Exception {
			// given
			PageRequestDto pageRequest = new PageRequestDto(0, 10);
			String request = objectMapper.writeValueAsString(pageRequest);

			// when & then
			mockMvc.perform(get("/agenda/profile/history/list/" + user.getIntraId())
					.header("Authorization", "Bearer " + accessToken)
					.param("page", "0")
					.param("size", "10")
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isBadRequest());
		}
	}
}
