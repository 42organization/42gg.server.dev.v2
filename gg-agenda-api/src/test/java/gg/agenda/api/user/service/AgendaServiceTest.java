package gg.agenda.api.user.service;

import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gg.data.agenda.Agenda;
import gg.repo.agenda.AgendaRepository;
import gg.utils.annotation.UnitTest;

@UnitTest
class AgendaServiceTest {

	@Mock
	AgendaRepository agendaRepository;

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

			// when
			agendaService.findAgenda(agendaKey);

			// then
			verify(agendaRepository, times(1)).findAgendaByKey(agendaKey);
		}
	}
}