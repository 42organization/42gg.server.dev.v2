package gg.agenda.api.admin.agendateam.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaTeamAdminRepository;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.NotExistException;

@UnitTest
public class AgendaTeamAdminServiceTest {

	@Mock
	private AgendaAdminRepository agendaAdminRepository;

	@Mock
	private AgendaTeamAdminRepository agendaTeamAdminRepository;

	@InjectMocks
	private AgendaTeamAdminService agendaTeamAdminService;

	@Nested
	@DisplayName("Admin AgendaTeam 전체 조회")
	class GetAgendaTeamListAdmin {

		@Test
		@DisplayName("Admin AgendaTeam 전체 조회 성공")
		void getAgendaTeamListAdminSuccess() {
			// given
			Agenda agenda = Agenda.builder().build();
			List<AgendaTeam> announcements = new ArrayList<>();
			Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
			when(agendaAdminRepository.findByAgendaKey(any(UUID.class))).thenReturn(Optional.of(agenda));
			when(agendaTeamAdminRepository.findAllByAgenda(any(Agenda.class), any(Pageable.class)))
				.thenReturn(new PageImpl<>(announcements));

			// when
			List<AgendaTeam> result = agendaTeamAdminService.getAgendaTeamList(agenda.getAgendaKey(), pageable);

			// then
			verify(agendaAdminRepository, times(1)).findByAgendaKey(any(UUID.class));
			verify(agendaTeamAdminRepository, times(1))
				.findAllByAgenda(any(Agenda.class), any(Pageable.class));
			assertThat(result).isNotNull();
		}

		@Test
		@DisplayName("Admin AgendaTeam 전체 조회 실패 - Agenda 없음")
		void getAgendaTeamListAdminFailedWithNoAgenda() {
			// given
			Pageable pageable = mock(Pageable.class);
			when(agendaAdminRepository.findByAgendaKey(any(UUID.class))).thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaTeamAdminService.getAgendaTeamList(UUID.randomUUID(), pageable));
		}
	}
}
