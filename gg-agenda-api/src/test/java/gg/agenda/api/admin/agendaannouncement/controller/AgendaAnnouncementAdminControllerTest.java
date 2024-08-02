package gg.agenda.api.admin.agendaannouncement.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaAnnouncementAdminRepository;
import gg.agenda.api.admin.agendaannouncement.controller.request.AgendaAnnouncementAdminUpdateReqDto;
import gg.agenda.api.user.agendaannouncement.controller.response.AgendaAnnouncementResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.user.User;
import gg.utils.AgendaTestDataUtils;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.PageRequestDto;
import gg.utils.fixture.agenda.AgendaAnnouncementFixture;
import gg.utils.fixture.agenda.AgendaFixture;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class AgendaAnnouncementAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestDataUtils testDataUtils;

	@Autowired
	private AgendaFixture agendaFixture;

	@Autowired
	private AgendaAnnouncementFixture agendaAnnouncementFixture;

	@Autowired
	private AgendaTestDataUtils agendaTestDataUtils;

	@Autowired
	EntityManager em;

	@Autowired
	AgendaAdminRepository agendaAdminRepository;

	@Autowired
	AgendaAnnouncementAdminRepository agendaAnnouncementAdminRepository;

	private User user;

	private String accessToken;

	@BeforeEach
	void setUp() {
		user = testDataUtils.createAdminUser();
		accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
	}

	@Nested
	@DisplayName("Admin AgendaAnnouncement 상세 조회")
	class GetAgendaAnnouncementListAdmin {

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3, 4, 5, 6})
		@DisplayName("Admin AgendaAnnouncement 상세 조회 성공")
		void getAgendaAnnouncementAdminSuccess(int page) throws Exception {
			// given
			int size = 10;
			Agenda agenda = agendaFixture.createAgenda();
			List<AgendaAnnouncement> announcements =
				agendaAnnouncementFixture.createAgendaAnnouncementList(agenda, 37);
			PageRequestDto pageDto = new PageRequestDto(page, size);
			String request = objectMapper.writeValueAsString(pageDto);

			// when
			String response = mockMvc.perform(get("/agenda/admin/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaAnnouncementResDto[] result =
				objectMapper.readValue(response, AgendaAnnouncementResDto[].class);

			// then
			assertThat(result).hasSize(((page - 1) * size) < announcements.size()
				? Math.min(size, announcements.size() - (page - 1) * size) : 0);
			announcements.sort((a, b) -> b.getId().compareTo(a.getId()));
			for (int i = 0; i < result.length; i++) {
				assertThat(result[i].getId()).isEqualTo(announcements.get(i + (page - 1) * size).getId());
			}
		}

		@Test
		@DisplayName("Admin AgendaAnnouncement 상세 조회 성공 - 빈 리스트 반환")
		void getAgendaAnnouncementAdminSuccessWithNoContent() throws Exception {
			// given
			int page = 1;
			int size = 10;
			Agenda agenda = agendaFixture.createAgenda();
			PageRequestDto pageDto = new PageRequestDto(page, size);
			String request = objectMapper.writeValueAsString(pageDto);

			// when
			String response = mockMvc.perform(get("/agenda/admin/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", agenda.getAgendaKey().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			AgendaAnnouncementResDto[] result =
				objectMapper.readValue(response, AgendaAnnouncementResDto[].class);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Admin AgendaAnnouncement 상세 조회 실패 - Agenda가 없는 경우")
		void getAgendaAnnouncementAdminFailedWithNoAgenda() throws Exception {
			// given
			int page = 1;
			int size = 10;
			PageRequestDto pageDto = new PageRequestDto(page, size);
			String request = objectMapper.writeValueAsString(pageDto);

			// expected
			mockMvc.perform(get("/agenda/admin/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.param("agenda_key", UUID.randomUUID().toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("Admin AgendaAnnouncement 수정 및 삭제")
	class UpdateAgendaAnnouncementAdmin {
		@Test
		@DisplayName("Admin AgendaAnnouncement 수정 성공")
		void updateAgendaAnnouncementAdminSuccess() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaAnnouncement announcement = agendaAnnouncementFixture.createAgendaAnnouncement(agenda);
			AgendaAnnouncementAdminUpdateReqDto updateReqDto = AgendaAnnouncementAdminUpdateReqDto.builder()
				.id(announcement.getId()).isShow(!announcement.getIsShow())
				.title("수정된 공지사항 제목").content("수정된 공지사항 내용").build();
			String request = objectMapper.writeValueAsString(updateReqDto);

			// when
			mockMvc.perform(patch("/agenda/admin/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNoContent());
			AgendaAnnouncement result = agendaAnnouncementAdminRepository
				.findById(announcement.getId()).orElseThrow();

			// then
			assertThat(result.getTitle()).isEqualTo(updateReqDto.getTitle());
			assertThat(result.getContent()).isEqualTo(updateReqDto.getContent());
			assertThat(result.getIsShow()).isEqualTo(updateReqDto.getIsShow());
		}

		@Test
		@DisplayName("Admin AgendaAnnouncement 수정 실패 - 존재하지 않는 공지사항")
		void updateAgendaAnnouncementAdminFailedWithNoAnnouncement() throws Exception {
			// given
			AgendaAnnouncementAdminUpdateReqDto updateReqDto = AgendaAnnouncementAdminUpdateReqDto.builder()
				.id(1L).isShow(true).title("수정된 공지사항 제목").content("수정된 공지사항 내용").build();
			String request = objectMapper.writeValueAsString(updateReqDto);

			// expected
			mockMvc.perform(patch("/agenda/admin/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("Admin AgendaAnnouncement 수정 실패 - Update Dto에 id가 없는 경우")
		void updateAgendaAnnouncementAdminFailedWithNoReqId() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaAnnouncement announcement = agendaAnnouncementFixture.createAgendaAnnouncement(agenda);
			AgendaAnnouncementAdminUpdateReqDto updateReqDto = AgendaAnnouncementAdminUpdateReqDto.builder()
				.isShow(!announcement.getIsShow()).title("수정된 공지사항 제목").content("수정된 공지사항 내용").build();
			String request = objectMapper.writeValueAsString(updateReqDto);

			// expected
			mockMvc.perform(patch("/agenda/admin/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin AgendaAnnouncement 수정 실패 - title이 너무 긴 경우")
		void updateAgendaAnnouncementAdminFailedWithTooLongTitle() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaAnnouncement announcement = agendaAnnouncementFixture.createAgendaAnnouncement(agenda);
			AgendaAnnouncementAdminUpdateReqDto updateReqDto = AgendaAnnouncementAdminUpdateReqDto.builder()
				.id(announcement.getId()).isShow(!announcement.getIsShow())
				.title("수정된 공지사항 제목".repeat(10)).content("수정된 공지사항 내용").build();
			String request = objectMapper.writeValueAsString(updateReqDto);

			// expected
			mockMvc.perform(patch("/agenda/admin/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("Admin AgendaAnnouncement 수정 실패 - content이 너무 긴 경우")
		void updateAgendaAnnouncementAdminFailedWithTooLongContent() throws Exception {
			// given
			Agenda agenda = agendaFixture.createAgenda();
			AgendaAnnouncement announcement = agendaAnnouncementFixture.createAgendaAnnouncement(agenda);
			AgendaAnnouncementAdminUpdateReqDto updateReqDto = AgendaAnnouncementAdminUpdateReqDto.builder()
				.id(announcement.getId()).isShow(!announcement.getIsShow())
				.title("수정된 공지사항 제목").content("수정된 공지사항 내용".repeat(100)).build();
			String request = objectMapper.writeValueAsString(updateReqDto);

			// expected
			mockMvc.perform(patch("/agenda/admin/announcement")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isBadRequest());
		}
	}
}
