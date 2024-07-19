package gg.agenda.api.admin.agenda.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.data.agenda.Agenda;
import gg.utils.annotation.UnitTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UnitTest
public class AgendaAdminServiceTest {

	@Mock
	AgendaAdminRepository agendaAdminRepository;

	@InjectMocks
	AgendaAdminService agendaAdminService;

	@Nested
	@DisplayName("Admin Agenda 상세 조회")
	class GetAgendaAdmin {

		@Test
		@DisplayName("Admin Agenda 상세 조회 성공")
		void findAgendaByAgendaKeySuccessAdmin() {
			// given
			Pageable pageable = mock(Pageable.class);
			List<Agenda> agendas = new ArrayList<>();
			agendas.add(Agenda.builder().build());
			Page<Agenda> page = new PageImpl<>(agendas);
			when(agendaAdminRepository.findAll(pageable)).thenReturn(page);

			// when
			List<Agenda> result = agendaAdminService.getAgendaRequestList(pageable);

			// then
			verify(agendaAdminRepository, times(1)).findAll(pageable);
			assertThat(result).isNotEmpty();
		}

		@Test
		@DisplayName("Admin Agenda 상세 조회 성공 - 빈 리스트인 경우")
		void findAgendaByAgendaKeySuccessAdminWithNoContent() {
			// given
			Pageable pageable = mock(Pageable.class);
			Page<Agenda> page = new PageImpl<>(List.of());
			when(agendaAdminRepository.findAll(pageable)).thenReturn(page);

			// when
			List<Agenda> result = agendaAdminService.getAgendaRequestList(pageable);

			// then
			verify(agendaAdminRepository, times(1)).findAll(pageable);
			assertThat(result).isEmpty();
		}
	}
}
