package gg.agenda.api.user.service;

import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gg.agenda.api.user.agenda.service.AgendaService;
import gg.data.agenda.Agenda;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.repo.agenda.AgendaRepository;
import gg.utils.annotation.UnitTest;

@UnitTest
class AgendaServiceTest {

	@Mock
	AgendaRepository agendaRepository;

	@Mock
	AgendaAnnouncementRepository agendaAnnouncementRepository;

	@InjectMocks
	AgendaService agendaService;

	@Nested
	class GetAgenda {

		@Test
		@DisplayName("Agenda 단건 조회")
		void test() {
			// given
			UUID agendaKey = UUID.randomUUID();
			Agenda agenda = mock(Agenda.class);
			when(agendaRepository.findAgendaByKey(agendaKey)).thenReturn(Optional.of(agenda));
			when(agendaAnnouncementRepository.findLatestByAgenda(agenda)).thenReturn(Optional.empty());

			// when
			agendaService.findAgendaWithLatestAnnouncement(agendaKey);

			// then
			verify(agendaRepository, times(1)).findAgendaByKey(agendaKey);
			verify(agendaAnnouncementRepository, times(1)).findLatestByAgenda(agenda);
		}
	}
}