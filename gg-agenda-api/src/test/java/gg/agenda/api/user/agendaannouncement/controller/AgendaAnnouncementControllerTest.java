package gg.agenda.api.user.agendaannouncement.controller;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.agenda.api.AgendaMockData;
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
		void createAgendaAnnouncementSuccess() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - title이 null인 경우")
		void createAgendaAnnouncementFailedWithNoTitle() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - title이 빈 문자열 경우")
		void createAgendaAnnouncementFailedWithEmptyTitle() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - Content가 null인 경우")
		void createAgendaAnnouncementFailedWithNoContent() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - Content가 빈 문자열 경우")
		void createAgendaAnnouncementFailedWithEmptyContent() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - Agenda가 없는 경우")
		void createAgendaAnnouncementFailedWithNoAgenda() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("AgendaAnnouncement 생성 실패 - 개최자가 아닌 경우")
		void createAgendaAnnouncementFailedWithNotHost() {
			// given
			// when
			// then
		}
	}
}
