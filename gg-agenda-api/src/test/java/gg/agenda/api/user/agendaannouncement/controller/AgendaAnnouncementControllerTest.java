package gg.agenda.api.user.agendaannouncement.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gg.agenda.api.AgendaMockData;
import gg.agenda.api.user.agendaannouncement.controller.request.AgendaAnnouncementCreateReqDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.user.User;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class AgendaAnnouncementControllerTest {

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

	@Autowired
	AgendaAnnouncementRepository agendaAnnouncementRepository;

	private User user;

	private String accessToken;

	@BeforeEach
	void setUp() {
		user = testDataUtils.createNewUser();
		accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
	}

	@Nested
	@DisplayName("AgendaAnnouncement 생성")
	class createAgendaAnnouncement {

		@Test
		@DisplayName("AgendaAnnouncement 생성 성공")
		void createAgendaAnnouncementSuccess() throws Exception {
			// given
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("title").content("content").build();

			// when
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isCreated());
			Optional<AgendaAnnouncement> latestAnnounce = agendaAnnouncementRepository.findLatestByAgenda(agenda);

			// then
			assertThat(latestAnnounce).isPresent();
			latestAnnounce.ifPresent(announcement -> assertThat(announcement.getTitle()).isEqualTo(dto.getTitle()));
			latestAnnounce.ifPresent(announcement -> assertThat(announcement.getContent()).isEqualTo(dto.getContent()));
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - title이 null인 경우")
		void createAgendaAnnouncementFailedWithNoTitle() throws Exception {
			// given
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.content("content").build();	// title이 null인 경우

			// expected
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - title이 빈 문자열 경우")
		void createAgendaAnnouncementFailedWithEmptyTitle() throws Exception {
			// given
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("").content("content").build();	// title이 empty인 경우

			// expected
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - Content가 null인 경우")
		void createAgendaAnnouncementFailedWithNoContent() throws Exception {
			// given
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("title").build();	// content가 null인 경우

			// expected
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - Content가 빈 문자열 경우")
		void createAgendaAnnouncementFailedWithEmptyContent() throws Exception {
			// given
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("title").content("").build();	// content가 empty인 경우

			// expected
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - Agenda가 없는 경우")
		void createAgendaAnnouncementFailedWithNoAgenda() throws Exception {
			// given
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("title").content("content").build();

			// expected
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", UUID.randomUUID().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - 개최자가 아닌 경우")
		void createAgendaAnnouncementFailedWithNotHost() throws Exception {
			// given
			Agenda agenda = agendaMockData.createAgenda("another");	// 다른 사용자가 생성한 Agenda
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("title").content("content").build();

			// expected
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isForbidden());
		}
	}

	@Nested
	@DisplayName("AgendaAnnouncement 전체 조회")
	class getAgendaAnnouncementList {

		@Test
		@DisplayName("AgendaAnnouncement 전체 조회 성공")
		void getAgendaAnnouncementListSuccess() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("AgendaAnnouncement 전체 조회 성공 - 데이터 없는 경우")
		void getAgendaAnnouncementListSuccessWhenNoEntity() {
			// given
			// when
			// then
		}
	}
}
