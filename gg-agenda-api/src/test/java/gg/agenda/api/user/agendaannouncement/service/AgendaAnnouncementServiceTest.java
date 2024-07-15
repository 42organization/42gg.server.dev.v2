package gg.agenda.api.user.agendaannouncement.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.utils.annotation.UnitTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UnitTest
public class AgendaAnnouncementServiceTest {

	@Mock
	private AgendaAnnouncementRepository agendaAnnouncementRepository;

	@InjectMocks
	private AgendaAnnouncementService agendaAnnouncementService;

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

	@Nested
	@DisplayName("마지막 AgendaAnnouncement 조회")
	class getAgendaAnnouncementLatest {

		@Test
		@DisplayName("마지막 AgendaAnnouncement 조회 성공")
		void getAgendaAnnouncementLatestSuccess() {
			// given
			// when
			// then
		}

		@Test
		@DisplayName("마지막 AgendaAnnouncement 조회 성공 - 데이터 없는 경우")
		void getAgendaAnnouncementLatestSuccessWhenNoEntity() {
			// given
			// when
			// then
		}
	}
}
