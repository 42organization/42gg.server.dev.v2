package gg.agenda.api.admin.agendaannouncement.service;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaAnnouncementAdminRepository;
import gg.utils.annotation.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@UnitTest
public class AgendaAnnouncementAdminServiceTest {

	@Mock
	private AgendaAdminRepository agendaAdminRepository;

	@Mock
	private AgendaAnnouncementAdminRepository agendaAnnouncementAdminRepository;

	@InjectMocks
	private AgendaAnnouncementAdminService agendaAnnouncementAdminService;

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
