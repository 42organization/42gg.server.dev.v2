package gg.agenda.api.user.agenda.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.agenda.api.AgendaMockData;
import gg.agenda.api.user.agenda.controller.request.AgendaAwardsReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaTeamAward;
import gg.agenda.api.user.agenda.controller.response.AgendaKeyResDto;
import gg.agenda.api.user.agenda.controller.response.AgendaResDto;
import gg.agenda.api.user.agenda.controller.response.AgendaSimpleResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.utils.AgendaTestDataUtils;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.PageRequestDto;
import gg.utils.fixture.agenda.AgendaFixture;
import gg.utils.fixture.agenda.AgendaTeamFixture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class AgendaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestDataUtils testDataUtils;

	@Autowired
	private AgendaMockData agendaMockData;

	@Autowired
	private AgendaFixture agendaFixture;

	@Autowired
	private AgendaTeamFixture agendaTeamFixture;

	@Autowired
	private AgendaTestDataUtils agendaTestDataUtils;

	@Autowired
	EntityManager em;

	@Autowired
	AgendaRepository agendaRepository;

	@Autowired
	AgendaTeamRepository agendaTeamRepository;

	private User user;

	private String accessToken;

	@BeforeEach
	void setUp() {
		user = testDataUtils.createNewUser();
		accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
	}

	@Nested
	@DisplayName("Agenda 상세 조회")
	class GetAgenda {

		@Test
		@DisplayName("agenda_id에 해당하는 Agenda를 상세 조회합니다.")
		void getAgendaSuccess() throws Exception {
			// given
			Agenda agenda = agendaMockData.createOfficialAgenda();
			AgendaAnnouncement announcement = agendaMockData.createAgendaAnnouncement(agenda);

			// when
			String response = mockMvc.perform(get("/agenda")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString()))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			AgendaResDto result = objectMapper.readValue(response, AgendaResDto.class);

			// then
			assertThat(result.getAgendaTitle()).isEqualTo(agenda.getTitle());
			assertThat(result.getAnnouncementTitle()).isEqualTo(announcement.getTitle());
		}

		@Test
		@DisplayName("announce가 없는 경우 announcementTitle를 빈 문자열로 반환합니다.")
		void getAgendaWithNoAnnounce() throws Exception {
			// given
			Agenda agenda = agendaMockData.createOfficialAgenda();

			// when
			String response = mockMvc.perform(get("/agenda")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString()))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			AgendaResDto result = objectMapper.readValue(response, AgendaResDto.class);

			// then
			assertThat(result.getAgendaTitle()).isEqualTo(agenda.getTitle());
			assertThat(result.getAnnouncementTitle()).isEqualTo("");
		}

		@Test
		@DisplayName("announce가 여러 개인 경우 가장 최근 작성된 announce를 반환합니다.")
		void getAgendaWithLatestAnnounce() throws Exception {
			// given
			Agenda agenda = agendaMockData.createOfficialAgenda();
			AgendaAnnouncement announcement1 = agendaMockData.createAgendaAnnouncement(agenda);
			AgendaAnnouncement announcement2 = agendaMockData.createAgendaAnnouncement(agenda);
			AgendaAnnouncement announcement3 = agendaMockData.createAgendaAnnouncement(agenda);

			// when
			String response = mockMvc.perform(get("/agenda")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString()))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			AgendaResDto result = objectMapper.readValue(response, AgendaResDto.class);

			// then
			assertThat(result.getAgendaTitle()).isEqualTo(agenda.getTitle());
			assertThat(result.getAnnouncementTitle()).isNotEqualTo(announcement1.getTitle());
			assertThat(result.getAnnouncementTitle()).isNotEqualTo(announcement2.getTitle());
			assertThat(result.getAnnouncementTitle()).isEqualTo(announcement3.getTitle());
		}

		@Test
		@DisplayName("agenda_key가 잘못된 경우 400를 반환합니다.")
		void getAgendaFailedWhenInvalidKey() throws Exception {
			// given
			Agenda agenda = agendaMockData.createOfficialAgenda();
			AgendaAnnouncement announcement = agendaMockData.createAgendaAnnouncement(agenda);

			// expected
			mockMvc.perform(get("/agenda")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", "invalid_key"))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("agenda_key에 해당하는 agenda가 없는 경우 404를 반환합니다.")
		void getAgendaFailedWhenNoContent() throws Exception {
			// given
			UUID invalidKey = UUID.randomUUID();

			// expected
			mockMvc.perform(get("/agenda")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", invalidKey.toString()))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("Agenda 현황 전체 조회")
	class GetAgendaListCurrent {

		@Test
		@DisplayName("Official과 Deadline이 빠른 순으로 정렬하여 반환합니다.")
		void getAgendaListSuccess() throws Exception {
			// given
			List<Agenda> officialAgendaList = agendaMockData.createOfficialAgendaList(3, AgendaStatus.OPEN);
			List<Agenda> nonOfficialAgendaList = agendaMockData
				.createNonOfficialAgendaList(6, AgendaStatus.OPEN);

			// when
			String response = mockMvc.perform(get("/agenda/list")
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			AgendaSimpleResDto[] result = objectMapper.readValue(response, AgendaSimpleResDto[].class);

			// then
			assertThat(result.length).isEqualTo(officialAgendaList.size() + nonOfficialAgendaList.size());
			for (int i = 0; i < result.length; i++) {
				assertThat(result[i].getIsOfficial()).isEqualTo(i < officialAgendaList.size());
				if (i == 0 || i == officialAgendaList.size()) {
					continue;
				}
				assertThat(result[i].getAgendaDeadLine()).isBefore(result[i - 1].getAgendaDeadLine());
			}
		}

		@Test
		@DisplayName("진행 중인 Agenda가 없는 경우 빈 리스트를 반환합니다.")
		void getAgendaListSuccessWithNoAgenda() throws Exception {
			// given
			agendaMockData.createOfficialAgendaList(3, AgendaStatus.FINISH);
			agendaMockData.createNonOfficialAgendaList(6, AgendaStatus.CANCEL);

			// when
			String response = mockMvc.perform(get("/agenda/list")
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			AgendaSimpleResDto[] result = objectMapper.readValue(response, AgendaSimpleResDto[].class);

			// then
			assertThat(result.length).isEqualTo(0);
		}
	}

	@Nested
	@DisplayName("Agenda 생성하기")
	class CreateAgenda {

		@Test
		@DisplayName("Agenda를 생성합니다.")
		void createAgendaSuccess() throws Exception {
			// given
			AgendaCreateReqDto dto = AgendaCreateReqDto.builder()
				.agendaTitle("title").agendaContents("content")
				.agendaDeadLine(LocalDateTime.now().plusDays(3))
				.agendaStartTime(LocalDateTime.now().plusDays(5))
				.agendaEndTime(LocalDateTime.now().plusDays(7))
				.agendaMinTeam(2).agendaMaxTeam(5).agendaMinPeople(1).agendaMaxPeople(5)
				.agendaIsRanking(true).agendaIsOfficial(true).agendaLocation(Location.SEOUL).build();
			String request = objectMapper.writeValueAsString(dto);

			// when
			String response = mockMvc.perform(post("/agenda/create")
					.header("Authorization", "Bearer " + accessToken)
					.contentType("application/json")
					.content(request))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
			AgendaKeyResDto result = objectMapper.readValue(response, AgendaKeyResDto.class);
			Optional<Agenda> agenda = agendaRepository.findByAgendaKey(result.getAgendaKey());

			// then
			assertThat(agenda.isPresent()).isTrue();
			assertThat(agenda.get().getTitle()).isEqualTo(dto.getAgendaTitle());
			assertThat(agenda.get().getContent()).isEqualTo(dto.getAgendaContent());
		}

		@Test
		@DisplayName("deadline이 startTime보다 미래인 경우 400을 반환합니다.")
		void createAgendaFailedWhenDeadlineIsAfterStartTime() throws Exception {
			// given
			AgendaCreateReqDto dto = AgendaCreateReqDto.builder()
				.agendaTitle("title").agendaContents("content")
				.agendaDeadLine(LocalDateTime.now().plusDays(6))
				.agendaStartTime(LocalDateTime.now().plusDays(5))
				.agendaEndTime(LocalDateTime.now().plusDays(7))
				.agendaMinTeam(2).agendaMaxTeam(5).agendaMinPeople(1).agendaMaxPeople(5)
				.agendaIsRanking(true).agendaIsOfficial(true).agendaLocation(Location.SEOUL).build();
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/create")
					.header("Authorization", "Bearer " + accessToken)
					.contentType("application/json")
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("deadline이 endTime보다 미래인 경우 400을 반환합니다.")
		void createAgendaFailedWhenDeadlineIsAfterEndTime() throws Exception {
			// given
			AgendaCreateReqDto dto = AgendaCreateReqDto.builder()
				.agendaTitle("title").agendaContents("content")
				.agendaDeadLine(LocalDateTime.now().plusDays(3))
				.agendaStartTime(LocalDateTime.now().plusDays(5))
				.agendaEndTime(LocalDateTime.now().plusDays(4))
				.agendaMinTeam(2).agendaMaxTeam(5).agendaMinPeople(1).agendaMaxPeople(5)
				.agendaIsRanking(true).agendaIsOfficial(true).agendaLocation(Location.SEOUL).build();
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/create")
					.header("Authorization", "Bearer " + accessToken)
					.contentType("application/json")
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("startTime이 endTime보다 미래인 경우 400을 반환합니다.")
		void createAgendaFailedWhenStartTimeIsAfterEndTime() throws Exception {
			// given
			AgendaCreateReqDto dto = AgendaCreateReqDto.builder()
				.agendaTitle("title").agendaContents("content")
				.agendaDeadLine(LocalDateTime.now().plusDays(3))
				.agendaStartTime(LocalDateTime.now().plusDays(7))
				.agendaEndTime(LocalDateTime.now().plusDays(5))
				.agendaMinTeam(2).agendaMaxTeam(5).agendaMinPeople(1).agendaMaxPeople(5)
				.agendaIsRanking(true).agendaIsOfficial(true).agendaLocation(Location.SEOUL).build();
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/create")
					.header("Authorization", "Bearer " + accessToken)
					.contentType("application/json")
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("min team이 max team보다 큰 경우 400을 반환합니다.")
		void createAgendaFailedWhenMinTeamGreaterThanMaxTeam() throws Exception {
			// given
			AgendaCreateReqDto dto = AgendaCreateReqDto.builder()
				.agendaTitle("title").agendaContents("content")
				.agendaDeadLine(LocalDateTime.now().plusDays(3))
				.agendaStartTime(LocalDateTime.now().plusDays(5))
				.agendaEndTime(LocalDateTime.now().plusDays(7))
				.agendaMinTeam(7).agendaMaxTeam(5).agendaMinPeople(1).agendaMaxPeople(5)
				.agendaIsRanking(true).agendaIsOfficial(true).agendaLocation(Location.SEOUL).build();
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/create")
					.header("Authorization", "Bearer " + accessToken)
					.contentType("application/json")
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("min people이 max people보다 큰 경우 400을 반환합니다.")
		void createAgendaFailedWhenMinPeopleGreaterThanMaxPeople() throws Exception {
			// given
			AgendaCreateReqDto dto = AgendaCreateReqDto.builder()
				.agendaTitle("title").agendaContents("content")
				.agendaDeadLine(LocalDateTime.now().plusDays(3))
				.agendaStartTime(LocalDateTime.now().plusDays(5))
				.agendaEndTime(LocalDateTime.now().plusDays(7))
				.agendaMinTeam(2).agendaMaxTeam(5).agendaMinPeople(6).agendaMaxPeople(5)
				.agendaIsRanking(true).agendaIsOfficial(true).agendaLocation(Location.SEOUL).build();
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/create")
					.header("Authorization", "Bearer " + accessToken)
					.contentType("application/json")
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@ParameterizedTest
		@ValueSource(ints = {1, 0, -1})
		@DisplayName("min team이 1 이하인 경우 400을 반환합니다.")
		void createAgendaFailedWhenNegativeMinTeam(int value) throws Exception {
			// given
			AgendaCreateReqDto dto = AgendaCreateReqDto.builder()
				.agendaTitle("title").agendaContents("content")
				.agendaDeadLine(LocalDateTime.now().plusDays(3))
				.agendaStartTime(LocalDateTime.now().plusDays(5))
				.agendaEndTime(LocalDateTime.now().plusDays(7))
				.agendaMinTeam(value).agendaMaxTeam(5).agendaMinPeople(1).agendaMaxPeople(5)
				.agendaIsRanking(true).agendaIsOfficial(true).agendaLocation(Location.SEOUL).build();
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/create")
					.header("Authorization", "Bearer " + accessToken)
					.contentType("application/json")
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@ParameterizedTest
		@ValueSource(ints = {0, -1})
		@DisplayName("min people이 0 이하인 경우 400을 반환합니다.")
		void createAgendaFailedWhenNegativeMinPeople(int value) throws Exception {
			// given
			AgendaCreateReqDto dto = AgendaCreateReqDto.builder()
				.agendaTitle("title").agendaContents("content")
				.agendaDeadLine(LocalDateTime.now().plusDays(3))
				.agendaStartTime(LocalDateTime.now().plusDays(5))
				.agendaEndTime(LocalDateTime.now().plusDays(7))
				.agendaMinTeam(2).agendaMaxTeam(5).agendaMinPeople(value).agendaMaxPeople(5)
				.agendaIsRanking(true).agendaIsOfficial(true).agendaLocation(Location.SEOUL).build();
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/create")
					.header("Authorization", "Bearer " + accessToken)
					.contentType("application/json")
					.content(request))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("Agenda 지난 목록 조회")
	class GetAgendaListHistory {

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3, 4})
		@DisplayName("지난 Agenda 목록을 조회합니다.")
		void getAgendaListHistorySuccess(int page) throws Exception {
			// given
			int totalCount = 35;
			int size = 10;
			List<Agenda> agendaHistory = agendaMockData.createAgendaHistory(totalCount);
			PageRequestDto pageRequestDto = new PageRequestDto(page, size);
			String req = objectMapper.writeValueAsString(pageRequestDto);

			// when
			String response = mockMvc.perform(get("/agenda/history")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(req))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaSimpleResDto[] result = objectMapper.readValue(response, AgendaSimpleResDto[].class);

			// then
			assertThat(result.length).isEqualTo(size * page < totalCount ? size : totalCount % size);
			for (int i = 0; i < result.length; i++) {
				assertThat(result[i].getAgendaTitle()).isEqualTo(agendaHistory.get(size * (page - 1) + i).getTitle());
				if (i == 0) {
					continue;
				}
				assertThat(result[i].getAgendaStartTime()).isBefore(result[i - 1].getAgendaStartTime());
			}
		}

		@Test
		@DisplayName("지난 Agenda가 없는 경우 빈 리스트를 반환합니다.")
		void getAgendaListHistoryWithNoContent() throws Exception {
			// given
			int page = 1;
			int size = 10;
			PageRequestDto pageRequestDto = new PageRequestDto(page, size);
			String req = objectMapper.writeValueAsString(pageRequestDto);

			// when
			String response = mockMvc.perform(get("/agenda/history")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(req))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaSimpleResDto[] result = objectMapper.readValue(response, AgendaSimpleResDto[].class);

			// then
			assertThat(result.length).isEqualTo(0);
		}

		@ParameterizedTest
		@ValueSource(ints = {0, -1})
		@DisplayName("page가 1보다 작은 경우 400을 반환합니다.")
		void getAgendaListHistoryWithInvalidPage(int page) throws Exception {
			// given
			int size = 10;
			PageRequestDto pageRequestDto = new PageRequestDto(page, size);
			String req = objectMapper.writeValueAsString(pageRequestDto);

			// expected
			mockMvc.perform(get("/agenda/history")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(req))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("page가 null인 경우 400을 반환합니다.")
		void getAgendaListHistoryWithoutPage() throws Exception {
			// given
			int size = 10;
			PageRequestDto pageRequestDto = new PageRequestDto(null, size);
			String req = objectMapper.writeValueAsString(pageRequestDto);

			// expected
			mockMvc.perform(get("/agenda/history")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(req))
				.andExpect(status().isBadRequest());
		}

		@ParameterizedTest
		@ValueSource(ints = {5, 6, 7, 8})
		@DisplayName("page가 실제 페이지 수보다 큰 경우 빈 리스트를 반환합니다.")
		void getAgendaListHistoryWithExcessPage(int page) throws Exception {
			// given
			int totalCount = 35;
			int size = 10;
			List<Agenda> agendaHistory = agendaMockData.createAgendaHistory(totalCount);
			PageRequestDto pageRequestDto = new PageRequestDto(page, size);
			String req = objectMapper.writeValueAsString(pageRequestDto);

			// when
			String response = mockMvc.perform(get("/agenda/history")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(req))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaSimpleResDto[] result = objectMapper.readValue(response, AgendaSimpleResDto[].class);

			// then
			assertThat(result.length).isEqualTo(0);
		}

		@ParameterizedTest
		@ValueSource(ints = {0, -1, 31})
		@DisplayName("size가 1 미만, 30 초과인 경우 400을 반환합니다.")
		void getAgendaListHistoryWithInvalidSize(int size) throws Exception {
			// given
			int page = 1;
			PageRequestDto pageRequestDto = new PageRequestDto(page, size);
			String req = objectMapper.writeValueAsString(pageRequestDto);

			// expected
			mockMvc.perform(get("/agenda/history")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(req))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("size가 null인 경우 size=20으로 조회합니다.")
		void getAgendaListHistoryWithoutSize() throws Exception {
			// given
			int page = 1;
			List<Agenda> agendaHistory = agendaMockData.createAgendaHistory(30);
			PageRequestDto pageRequestDto = new PageRequestDto(page, null);
			String req = objectMapper.writeValueAsString(pageRequestDto);

			// when
			String response = mockMvc.perform(get("/agenda/history")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(req))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaSimpleResDto[] result = objectMapper.readValue(response, AgendaSimpleResDto[].class);

			// then
			assertThat(result.length).isEqualTo(20);
			for (int i = 0; i < result.length; i++) {
				assertThat(result[i].getAgendaTitle()).isEqualTo(agendaHistory.get(i).getTitle());
				if (i == 0) {
					continue;
				}
				assertThat(result[i].getAgendaStartTime()).isBefore(result[i - 1].getAgendaStartTime());
			}
		}
	}

	@Nested
	@DisplayName("Agenda 종료 및 시상하기")
	class FinishAgenda {

		@Test
		@DisplayName("Agenda 종료 및 시상하기 성공 - 시상 대회인 경우")
		void finishAgendaSuccess() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(),
				LocalDateTime.now().minusDays(10), true, AgendaStatus.CONFIRM);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
						.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().awards(awards).build();
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// when
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isNoContent());
			Agenda result = em.createQuery("select a from Agenda a where a.agendaKey = :agendaKey", Agenda.class)
				.setParameter("agendaKey", agenda.getAgendaKey()).getSingleResult();

			// then
			assertThat(result.getStatus()).isEqualTo(AgendaStatus.FINISH);
			awards.forEach(award -> {
				AgendaTeam agendaTeam = em.createQuery(
						"select at from AgendaTeam at where at.agenda = :agenda and at.name = :teamName",
						AgendaTeam.class)
					.setParameter("agenda", agenda)
					.setParameter("teamName", award.getTeamName())
					.getSingleResult();
				assertThat(agendaTeam.getAward()).isEqualTo(award.getAwardName());
				assertThat(agendaTeam.getAwardPriority()).isEqualTo(award.getAwardPriority());
			});
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - 시상 대회에 시상 내역이 없는 경우")
		void finishAgendaFailedWithNoAwards() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(),
				LocalDateTime.now().minusDays(10), true, AgendaStatus.CONFIRM);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
						.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());

			// expected
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 성공 - 시상하지 않는 대회인 경우")
		void finishAgendaSuccessWithNoRanking() throws Exception {
			// given
			int teamSize = 10;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(),
				LocalDateTime.now().minusDays(10), false, AgendaStatus.CONFIRM);
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder()
				.awards(List.of())	// 시상하지 않는 대회도 빈 리스트를 전송해야합니다.
				.build();
			IntStream.range(0, teamSize)
				.forEach(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM));
			String request = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// when
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNoContent());
			Agenda result = em.createQuery("select a from Agenda a where a.agendaKey = :agendaKey", Agenda.class)
				.setParameter("agendaKey", agenda.getAgendaKey()).getSingleResult();

			// then
			assertThat(result.getStatus()).isEqualTo(AgendaStatus.FINISH);
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 성공 - 시상하지 않는 대회에 시상 내역이 들어온 경우")
		void finishAgendaSuccessWithNoRankAndAwards() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(),
				LocalDateTime.now().minusDays(10), false, AgendaStatus.CONFIRM);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
					.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().awards(awards).build();
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// when
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isNoContent());
			Agenda result = em.createQuery("select a from Agenda a where a.agendaKey = :agendaKey", Agenda.class)
				.setParameter("agendaKey", agenda.getAgendaKey()).getSingleResult();

			// then
			assertThat(result.getStatus()).isEqualTo(AgendaStatus.FINISH);
			awards.forEach(award -> {
				AgendaTeam agendaTeam = em.createQuery(
						"select at from AgendaTeam at where at.agenda = :agenda and at.name = :teamName",
						AgendaTeam.class)
					.setParameter("agenda", agenda)
					.setParameter("teamName", award.getTeamName())
					.getSingleResult();
				assertThat(agendaTeam.getAward()).isNotEqualTo(award.getAwardName());
				assertThat(agendaTeam.getAwardPriority()).isNotEqualTo(award.getAwardPriority());
			});
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - 존재하지 않는 팀에 대한 시상인 경우")
		void finishAgendaFailedWithInvalidTeam() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(),
				LocalDateTime.now().minusDays(10), true, AgendaStatus.CONFIRM);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
					.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().awards(awards).build();
			awards.add(AgendaTeamAward.builder()
				.teamName("invalid_team").awardName("prize").awardPriority(1).build());    // invalid team
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// expected
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - Agenda가 없는 경우")
		void finishAgendaFailedWithNoAgenda() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(), LocalDateTime.now().minusDays(10), true);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
					.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().awards(awards).build();
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			UUID invalidAgendaKey = UUID.randomUUID();    // invalid agenda key

			// expected
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", invalidAgendaKey.toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - 시상 내역이 없는 경우")
		void finishAgendaFailedWithoutAwards() throws Exception {
			// given
			int teamSize = 10;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(), LocalDateTime.now().minusDays(10), true);
			IntStream.range(0, teamSize).forEach(i ->
				agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM));
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().build();    // null
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// when
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - 시상 내역이 빈 리스트인 경우")
		void finishAgendaFailedWithEmptyAwards() throws Exception {
			// given
			int teamSize = 10;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(), LocalDateTime.now().minusDays(10), true);
			IntStream.range(0, teamSize).forEach(i ->
				agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM));
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder()
				.awards(List.of())    // empty
				.build();
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// when
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - 개최자가 아닌 경우")
		void finishAgendaFailedNotHost() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			User another = testDataUtils.createNewUser();
			Agenda agenda = agendaMockData.createAgenda(another.getIntraId(), LocalDateTime.now().minusDays(10), true);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
					.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().awards(awards).build();
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// when
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - 이미 확정된 경우")
		void finishAgendaFailedAlreadyConfirm() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(),
				LocalDateTime.now().minusDays(10), AgendaStatus.FINISH);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
						.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().awards(awards).build();
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// expected
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - 이미 취소된 경우")
		void finishAgendaFailedAlreadyCancel() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(),
				LocalDateTime.now().minusDays(10), AgendaStatus.CANCEL);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
					.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().awards(awards).build();
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// expected
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - 아직 시작하지 않은 경우")
		void finishAgendaFailedBeforeStartTime() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(),
				LocalDateTime.now().plusDays(1), AgendaStatus.OPEN);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
						.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().awards(awards).build();
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// expected
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - empty awardName")
		void finishAgendaFailedWithEmptyAwardName() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(), LocalDateTime.now().minusDays(10), true);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
						.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());
			awards.add(AgendaTeamAward.builder().teamName(agendaTeams.get(awardSize).getName())
				.awardName("").awardPriority(awardSize).build());    // empty award name
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().awards(awards).build();
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// expected
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - null awardName")
		void finishAgendaFailedWithNullAwardName() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(), LocalDateTime.now().minusDays(10), true);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
						.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());
			awards.add(AgendaTeamAward.builder().teamName(agendaTeams.get(awardSize).getName())
				.awardPriority(awardSize).build());    // null award name
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().awards(awards).build();
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// expected
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - empty teamName")
		void finishAgendaFailedWithEmptyTeamName() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(), LocalDateTime.now().minusDays(10), true);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
						.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());
			awards.add(AgendaTeamAward.builder().awardName("prize" + awardSize)
				.teamName("").awardPriority(awardSize).build());    // empty award name
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().awards(awards).build();
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// expected
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Agenda 종료 및 시상하기 실패 - null teamName")
		void finishAgendaFailedWithNullTeamName() throws Exception {
			// given
			int teamSize = 10;
			int awardSize = 3;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId(), LocalDateTime.now().minusDays(10), true);
			List<AgendaTeam> agendaTeams = IntStream.range(0, teamSize)
					.mapToObj(i -> agendaMockData.createAgendaTeam(agenda, "team" + i, AgendaTeamStatus.CONFIRM))
					.collect(Collectors.toList());
			List<AgendaTeamAward> awards = IntStream.range(0, awardSize)
					.mapToObj(i -> AgendaTeamAward.builder().teamName(agendaTeams.get(i).getName())
						.awardName("prize" + i).awardPriority(i + 1).build())
					.collect(Collectors.toList());
			awards.add(AgendaTeamAward.builder().awardName("prize" + awardSize)
				.awardPriority(awardSize).build());    // null award name
			AgendaAwardsReqDto agendaAwardsReqDto = AgendaAwardsReqDto.builder().awards(awards).build();
			String response = objectMapper.writeValueAsString(agendaAwardsReqDto);

			// expected
			mockMvc.perform(patch("/agenda/finish")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(response))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("Agenda 확정하기")
	class ConfirmAgenda {

		@Test
		@DisplayName("Agenda 확정하기 성공")
		void confirmAgendaSuccess() throws Exception {
			// given
			Agenda agenda = agendaTestDataUtils.createAgendaAndAgendaTeams(user.getIntraId(), 10, AgendaStatus.OPEN);
			List<AgendaTeam> openTeams = agendaTeamFixture.createAgendaTeamList(agenda, AgendaTeamStatus.OPEN, 3);

			// when
			mockMvc.perform(patch("/agenda/confirm")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNoContent());
			Optional<Agenda> confirmedAgenda = agendaRepository.findById(agenda.getId());
			List<AgendaTeam> openedTeams = agendaTeamRepository
				.findAllByAgendaAndStatus(agenda, AgendaTeamStatus.OPEN);
			List<AgendaTeam> canceledTeams = agendaTeamRepository
				.findAllByAgendaAndStatus(agenda, AgendaTeamStatus.CANCEL);

			// then
			assert (confirmedAgenda.isPresent());
			assertThat(confirmedAgenda.get().getStatus()).isEqualTo(AgendaStatus.CONFIRM);
			assertThat(openedTeams.size()).isEqualTo(0);
			assertThat(canceledTeams.size()).isEqualTo(openTeams.size());
		}

		@Test
		@DisplayName("Agenda 확정하기 실패 - 존재하지 않는 Agenda인 경우")
		void confirmAgendaFailedWithNoAgenda() throws Exception {
			// given
			// expected
			mockMvc.perform(patch("/agenda/confirm")
					.param("agenda_key", UUID.randomUUID().toString())
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("Agenda 확정하기 실패 - 개최자가 아닌 경우")
		void confirmAgendaFailedWithNotHost() throws Exception {
			// given
			Agenda agenda = agendaTestDataUtils.createAgendaAndAgendaTeams("not host", 10, AgendaStatus.OPEN);

			// when
			mockMvc.perform(patch("/agenda/confirm")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isForbidden());
		}

		@ParameterizedTest
		@EnumSource(value = AgendaStatus.class, names = {"CANCEL", "CONFIRM", "FINISH"})
		@DisplayName("Agenda 확정하기 실패 - 대회의 상태가 OPEN이 아닌 경우")
		void confirmAgendaFailedWithNotOpenAgenda(AgendaStatus status) throws Exception {
			// given
			Agenda agenda = agendaTestDataUtils.createAgendaAndAgendaTeams(user.getIntraId(), 10, status);
			agendaTeamFixture.createAgendaTeamList(agenda, AgendaTeamStatus.OPEN, 3);

			// expected
			mockMvc.perform(patch("/agenda/confirm")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest());
		}
	}
}
