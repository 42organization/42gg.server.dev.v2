package gg.agenda.api.admin.agendaannouncement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.data.user.User;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

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

	// TODO: add AgendaTestMockDataUtils.class;

	@Autowired
	EntityManager em;

	@Autowired
	AgendaAdminRepository agendaAdminRepository;

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

		@Test
		@DisplayName("Admin AgendaAnnouncement 상세 조회 성공")
		void getAgendaAnnouncementAdminSuccess() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("Admin AgendaAnnouncement 상세 조회 성공 - 빈 리스트 반환")
		void getAgendaAnnouncementAdminSuccessWithNoContent() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("Admin AgendaAnnouncement 상세 조회 실패 - Agenda가 없는 경우")
		void getAgendaAnnouncementAdminFailedWithNoAgenda() {
			// given
			// when
			// then
		}
	}
}
