package gg.agenda.api.user.agendaannouncement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.awt.print.Pageable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gg.agenda.api.AgendaMockData;
import gg.agenda.api.user.agendaannouncement.controller.request.AgendaAnnouncementCreateReqDto;
import gg.agenda.api.user.agendaannouncement.controller.response.AgendaAnnouncementResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.user.User;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.PageRequestDto;
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
	class CreateAgendaAnnouncement {

		@Test
		@DisplayName("AgendaAnnouncement 생성 성공")
		void createAgendaAnnouncementSuccess() throws Exception {
			// given
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("title").content("content").build();
			String request = objectMapper.writeValueAsString(dto);

			// when
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
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
				.content("content").build();    // title이 null인 경우
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - title이 빈 문자열 경우")
		void createAgendaAnnouncementFailedWithEmptyTitle() throws Exception {
			// given
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("").content("content").build();    // title이 empty인 경우
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - Content가 null인 경우")
		void createAgendaAnnouncementFailedWithNoContent() throws Exception {
			// given
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("title").build();    // content가 null인 경우
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - Content가 빈 문자열 경우")
		void createAgendaAnnouncementFailedWithEmptyContent() throws Exception {
			// given
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("title").content("").build();    // content가 empty인 경우
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - Agenda가 없는 경우")
		void createAgendaAnnouncementFailedWithNoAgenda() throws Exception {
			// given
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("title").content("content").build();
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", UUID.randomUUID().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - 개최자가 아닌 경우")
		void createAgendaAnnouncementFailedWithNotHost() throws Exception {
			// given
			Agenda agenda = agendaMockData.createAgenda("another");    // 다른 사용자가 생성한 Agenda
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("title").content("content").build();
			String request = objectMapper.writeValueAsString(dto);

			// expected
			mockMvc.perform(post("/agenda/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isForbidden());
		}
	}

	@Nested
	@DisplayName("AgendaAnnouncement 전체 조회")
	class GetAgendaAnnouncementList {

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3})
		@DisplayName("AgendaAnnouncement 전체 조회 성공")
		void getAgendaAnnouncementListSuccess(int page) throws Exception {
			// given
			int total = 35;
			int size = 10;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			agendaMockData.createAgendaAnnouncementList(agenda, 30, false);
			List<AgendaAnnouncement> announcements = agendaMockData
				.createAgendaAnnouncementList(agenda, total, true);
			PageRequestDto pageRequest = new PageRequestDto(page, size);
			String request = objectMapper.writeValueAsString(pageRequest);

			// when
			String response = mockMvc.perform(get("/agenda/announcement")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaAnnouncementResDto[] result = objectMapper.readValue(response, AgendaAnnouncementResDto[].class);

			// then
			assertThat(result).hasSize(size * page < total ? size : total % size);
			announcements.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
			for (int i = 0; i < result.length; i++) {
				assertThat(result[i].getId()).isEqualTo(announcements.get(i + (page - 1) * size).getId());
				assertThat(result[i].getTitle()).isEqualTo(announcements.get(i + (page - 1) * size).getTitle());
				assertThat(result[i].getContent()).isEqualTo(announcements.get(i + (page - 1) * size).getContent());
				if (i == 0) {
					continue;
				}
				assertThat(result[i].getId()).isLessThan(result[i - 1].getId());
			}
		}

		@Test
		@DisplayName("AgendaAnnouncement 전체 조회 성공 - 데이터 없는 경우")
		void getAgendaAnnouncementListSuccessWhenNoEntity() throws Exception {
			// given
			int page = 1;
			int size = 10;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			agendaMockData.createAgendaAnnouncementList(agenda, 30, false);
			PageRequestDto pageRequest = new PageRequestDto(page, size);
			String request = objectMapper.writeValueAsString(pageRequest);

			// when
			String response = mockMvc.perform(get("/agenda/announcement")
					.param("agenda_key", agenda.getAgendaKey().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaAnnouncementResDto[] result = objectMapper.readValue(response, AgendaAnnouncementResDto[].class);

			// then
			assertThat(result).hasSize(0);
		}

		@Test
		@DisplayName("AgendaAnnouncement 전체 조회 실패 - Agenda가 없는 경우")
		void getAgendaAnnouncementListFailedWithInvalidAgenda() throws Exception {
			// given
			int page = 1;
			int size = 10;
			Agenda agenda = agendaMockData.createAgenda(user.getIntraId());
			agendaMockData.createAgendaAnnouncementList(agenda, 30, false);
			agendaMockData.createAgendaAnnouncementList(agenda, 30, true);
			PageRequestDto pageRequest = new PageRequestDto(page, size);
			String request = objectMapper.writeValueAsString(pageRequest);

			// when
			mockMvc.perform(get("/agenda/announcement")
					.param("agenda_key", UUID.randomUUID().toString())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNotFound());
		}
	}
}
