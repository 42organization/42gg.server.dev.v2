package gg.agenda.api.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import gg.agenda.api.user.agenda.service.AgendaService;
import gg.data.agenda.Agenda;
import gg.data.agenda.type.AgendaStatus;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.repo.agenda.AgendaRepository;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.NotExistException;

@UnitTest
class AgendaServiceTest {

	@Mock
	AgendaRepository agendaRepository;

	@Mock
	AgendaAnnouncementRepository agendaAnnouncementRepository;

	@InjectMocks
	AgendaService agendaService;

	@Nested
	@DisplayName("Agenda 단건 조회")
	class GetAgenda {

		@Test
		@DisplayName("Agenda 단건 조회 성공")
		void getAgendaSuccess() {
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

		@Test
		@DisplayName("Agenda 단건 조회 실패")
		void getAgendaFailedWithnoAgenda() {
			// given
			UUID agendaKey = UUID.randomUUID();
			Agenda agenda = mock(Agenda.class);
			when(agendaRepository.findAgendaByKey(agendaKey)).thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class, () -> agendaService.findAgendaWithLatestAnnouncement(agendaKey));
			verify(agendaRepository, times(1)).findAgendaByKey(agendaKey);
			verify(agendaAnnouncementRepository, never()).findLatestByAgenda(agenda);
		}
	}

	@Nested
	@DisplayName("Agenda 현황 전체 조회")
	class GetAgendaListCurrent {

		@Test
		@DisplayName("Agenda 현황 전체를 반환합니다.")
		void getAgendaListSuccess() {
			// given
			List<Agenda> agendas = new ArrayList<>();
			IntStream.range(1, 10).forEach(i -> agendas.add(mock(Agenda.class)));
			when(agendaRepository.findAllByStatusIs(AgendaStatus.ON_GOING)).thenReturn(agendas);

			// when
			agendaService.findCurrentAgendaList();

			// then
			verify(agendaRepository, times(1)).findAllByStatusIs(any());
		}

		@Test
		@DisplayName("생성된 Agenda가 없는 경우 빈 리스트를 반환합니다.")
		void getAgendaListWithNoContent() {
			// given
			List<Agenda> agendas = new ArrayList<>();
			when(agendaRepository.findAllByStatusIs(AgendaStatus.ON_GOING)).thenReturn(agendas);

			// when
			agendaService.findCurrentAgendaList();

			// then

			verify(agendaRepository, times(1)).findAllByStatusIs(any());
		}
	}
}
