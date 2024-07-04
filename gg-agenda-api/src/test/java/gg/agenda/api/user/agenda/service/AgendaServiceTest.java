package gg.agenda.api.user.agenda.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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

import gg.agenda.api.user.agenda.controller.dto.AgendaCreateDto;
import gg.agenda.api.user.agenda.controller.dto.AgendaKeyResponseDto;
import gg.agenda.api.user.agenda.controller.dto.AgendaSimpleResponseDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.type.AgendaStatus;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.repo.agenda.AgendaRepository;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.NotExistException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
			when(agendaRepository.findByAgendaKey(agendaKey)).thenReturn(Optional.of(agenda));
			when(agendaAnnouncementRepository.findLatestByAgenda(agenda)).thenReturn(Optional.empty());

			// when
			agendaService.findAgendaWithLatestAnnouncement(agendaKey);

			// then
			verify(agendaRepository, times(1)).findByAgendaKey(agendaKey);
			verify(agendaAnnouncementRepository, times(1)).findLatestByAgenda(agenda);
		}

		@Test
		@DisplayName("Agenda 단건 조회 실패")
		void getAgendaFailedWithnoAgenda() {
			// given
			UUID agendaKey = UUID.randomUUID();
			Agenda agenda = mock(Agenda.class);
			when(agendaRepository.findByAgendaKey(agendaKey)).thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class, () -> agendaService.findAgendaWithLatestAnnouncement(agendaKey));
			verify(agendaRepository, times(1)).findByAgendaKey(agendaKey);
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
			int officialSize = 3;
			int nonOfficialSize = 6;
			List<Agenda> agendas = new ArrayList<>();
			IntStream.range(0, officialSize).forEach(i -> agendas.add(Agenda.builder().isOfficial(true)
				.deadline(LocalDateTime.now().plusDays(i + 3)).build()));
			IntStream.range(0, nonOfficialSize).forEach(i -> agendas.add(Agenda.builder().isOfficial(false)
				.deadline(LocalDateTime.now().plusDays(i + 3)).build()));
			when(agendaRepository.findAllByStatusIs(AgendaStatus.ON_GOING)).thenReturn(agendas);

			// when
			List<AgendaSimpleResponseDto> result = agendaService.findCurrentAgendaList();

			// then
			verify(agendaRepository, times(1)).findAllByStatusIs(any());
			for (int i = 0; i < result.size(); i++) {
				assertThat(result.get(i).getIsOfficial()).isEqualTo(i < officialSize);
				if (i == 0 || i == officialSize) {
					continue;
				}
				assertThat(result.get(i).getAgendaDeadLine()).isBefore(result.get(i - 1).getAgendaDeadLine());
			}
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

	@Nested
	@DisplayName("Agenda 생성")
	class CreateAgenda {

		@Test
		@DisplayName("Agenda 생성 성공")
		void createAgendaSuccess() {
			// given
			UserDto user = UserDto.builder().intraId("intraId").build();
			AgendaCreateDto agendaCreateDto = AgendaCreateDto.builder()
				.agendaDeadLine(LocalDateTime.now().plusDays(5))
				.agendaStartTime(LocalDateTime.now().plusDays(8))
				.agendaEndTime(LocalDateTime.now().plusDays(10))
				.agendaMinTeam(2).agendaMaxTeam(5)
				.agendaMinPeople(1).agendaMaxPeople(5)
				.build();
			Agenda agenda = Agenda.builder().build();
			when(agendaRepository.save(any(Agenda.class))).thenReturn(agenda);

			// when
			AgendaKeyResponseDto agendaKeyResponseDto = agendaService.addAgenda(agendaCreateDto, user);

			// then
			verify(agendaRepository, times(1)).save(any(Agenda.class));
			assertThat(agendaKeyResponseDto).isNotNull();
			assertThat(agendaKeyResponseDto.getAgendaKey()).isEqualTo(agenda.getAgendaKey());
		}
	}
}
