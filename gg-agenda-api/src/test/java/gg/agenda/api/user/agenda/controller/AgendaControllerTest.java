package gg.agenda.api.user.agenda.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gg.agenda.api.AgendaMockData;
import gg.agenda.api.user.agenda.controller.dto.AgendaCreateDto;
import gg.agenda.api.user.agenda.controller.dto.AgendaKeyResponseDto;
import gg.agenda.api.user.agenda.controller.dto.AgendaResponseDto;
import gg.agenda.api.user.agenda.controller.dto.AgendaSimpleResponseDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
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
	EntityManager em;

	private String accessToken;

	@BeforeEach
	void setUp() {
		User user = testDataUtils.createNewUser();
		accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
	}

	@Nested
	@DisplayName("Agenda 상세 조회")
	class GetAgenda {

		@Test
		@DisplayName("agenda_id에 해당하는 Agenda를 상세 조회합니다.")
		void test() throws Exception {
			// given
			Agenda agenda = agendaMockData.createOfficialAgenda();
			AgendaAnnouncement announcement = agendaMockData.createAgendaAnnouncement(agenda);

			// when
			String response = mockMvc.perform(get("/agenda")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString()))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			AgendaResponseDto result = objectMapper.readValue(response, AgendaResponseDto.class);

			// then
			assertThat(result.getAgendaTitle()).isEqualTo(agenda.getTitle());
			assertThat(result.getAnnouncementTitle()).isEqualTo(announcement.getTitle());
		}

		@Test
		@DisplayName("announce가 없는 경우 announcementTitle를 null로 반환합니다.")
		void test2() throws Exception {
			// given
			Agenda agenda = agendaMockData.createOfficialAgenda();

			// when
			String response = mockMvc.perform(get("/agenda")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString()))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			AgendaResponseDto result = objectMapper.readValue(response, AgendaResponseDto.class);

			// then
			assertThat(result.getAgendaTitle()).isEqualTo(agenda.getTitle());
			assertThat(result.getAnnouncementTitle()).isEqualTo(null);
		}

		@Test
		@DisplayName("announce가 여러 개인 경우 가장 최근 작성된 announce를 반환합니다.")
		void test3() throws Exception {
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
			AgendaResponseDto result = objectMapper.readValue(response, AgendaResponseDto.class);

			// then
			assertThat(result.getAgendaTitle()).isEqualTo(agenda.getTitle());
			assertThat(result.getAnnouncementTitle()).isNotEqualTo(announcement1.getTitle());
			assertThat(result.getAnnouncementTitle()).isNotEqualTo(announcement2.getTitle());
			assertThat(result.getAnnouncementTitle()).isEqualTo(announcement3.getTitle());
		}

		@Test
		@DisplayName("agenda_key가 잘못된 경우 400를 반환합니다.")
		void test4() throws Exception {
			// given
			Agenda agenda = agendaMockData.createOfficialAgenda();
			AgendaAnnouncement announcement = agendaMockData.createAgendaAnnouncement(agenda);

			// expected
			mockMvc.perform(get("/agenda")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", "invalid_key"))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("Agenda 현황 전체 조회")
	class GetAgendaListCurrent {

		@Test
		@DisplayName("Official과 Deadline이 빠른 순으로 정렬하여 반환합니다.")
		void getAgendaListSuccess() throws Exception {
			// given
			List<Agenda> officialAgendaList = agendaMockData.createOfficialAgendaList(3, AgendaStatus.ON_GOING);
			List<Agenda> nonOfficialAgendaList = agendaMockData
				.createNonOfficialAgendaList(6, AgendaStatus.ON_GOING);

			// when
			String response = mockMvc.perform(get("/agenda/list")
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			AgendaSimpleResponseDto[] result = objectMapper.readValue(response, AgendaSimpleResponseDto[].class);

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
			agendaMockData.createOfficialAgendaList(3, AgendaStatus.CONFIRM);
			agendaMockData.createNonOfficialAgendaList(6, AgendaStatus.CANCEL);

			// when
			String response = mockMvc.perform(get("/agenda/list")
					.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			AgendaSimpleResponseDto[] result = objectMapper.readValue(response, AgendaSimpleResponseDto[].class);

			// then
			assertThat(result.length).isEqualTo(0);
		}
	}

	@Nested
	@DisplayName("Agenda 생성하기")
	class CreateAgenda {

		@Test
		@DisplayName("Agenda를 생성합니다.")
		void CreateAgendaSuccess() throws Exception {
			// given
			AgendaCreateDto dto = AgendaCreateDto.builder()
				.agendaTitle("title").agendaContents("content")
				.agendaDeadLine(LocalDateTime.now().plusDays(3))
				.agendaStartTime(LocalDateTime.now().plusDays(5))
				.agendaEndTime(LocalDateTime.now().plusDays(7))
				.agendaMinTeam(2).agendaMaxTeam(5)
				.agendaMinPeople(1).agendaMaxPeople(5)
				.agendaLocation(Location.SEOUL)
				.build();
			String request = objectMapper.writeValueAsString(dto);

			// when
			String response = mockMvc.perform(post("/agenda/create")
					.header("Authorization", "Bearer " + accessToken)
					.contentType("application/json")
					.content(request))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
			AgendaKeyResponseDto agendaKeyResponseDto = objectMapper.readValue(response, AgendaKeyResponseDto.class);
			Agenda agenda = em.find(Agenda.class, agendaKeyResponseDto.getAgendaKey());

			// then
			assertThat(agenda.getTitle()).isEqualTo(dto.getAgendaTitle());
			assertThat(agenda.getContent()).isEqualTo(dto.getAgendaContent());
		}
	}
}
