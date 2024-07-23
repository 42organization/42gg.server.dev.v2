package gg.agenda.api.admin.agendaannouncement.service;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaAnnouncementAdminRepository;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.utils.annotation.UnitTest;
import gg.utils.exception.custom.NotExistException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
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
			Agenda agenda = Agenda.builder().build();
			List<AgendaAnnouncement> announcements = new ArrayList<>();
			for (int i = 0; i < 30; i++) {
				announcements.add(AgendaAnnouncement.builder().agenda(agenda).build());
			}
			Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
			when(agendaAdminRepository.findByAgendaKey(any(UUID.class))).thenReturn(Optional.of(agenda));
			when(agendaAnnouncementAdminRepository.findAllByAgenda(any(Agenda.class), any(Pageable.class)))
				.thenReturn(new PageImpl<>(announcements));

			// when
			agendaAnnouncementAdminService.getAgendaAnnouncementList(agenda.getAgendaKey(), pageable);

			// then
			verify(agendaAdminRepository, times(1)).findByAgendaKey(any(UUID.class));
			verify(agendaAnnouncementAdminRepository, times(1)).findAllByAgenda(any(Agenda.class), any(Pageable.class));
		}

		@Test
		@DisplayName("Admin AgendaAnnouncement 상세 조회 성공 - 빈 리스트 반환")
		void getAgendaAnnouncementAdminSuccessWithNoContent() {
			// given
			Agenda agenda = Agenda.builder().build();
			List<AgendaAnnouncement> announcements = new ArrayList<>();
			Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
			when(agendaAdminRepository.findByAgendaKey(any(UUID.class))).thenReturn(Optional.of(agenda));
			when(agendaAnnouncementAdminRepository.findAllByAgenda(any(Agenda.class), any(Pageable.class)))
				.thenReturn(new PageImpl<>(announcements));

			// when
			agendaAnnouncementAdminService.getAgendaAnnouncementList(agenda.getAgendaKey(), pageable);

			// then
			verify(agendaAdminRepository, times(1)).findByAgendaKey(any(UUID.class));
			verify(agendaAnnouncementAdminRepository, times(1)).findAllByAgenda(any(Agenda.class), any(Pageable.class));
		}

		@Test
		@DisplayName("Admin AgendaAnnouncement 상세 조회 실패 - Agenda가 없는 경우")
		void getAgendaAnnouncementAdminFailedWithNoAgenda() {
			// given
			Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
			when(agendaAdminRepository.findByAgendaKey(any(UUID.class))).thenReturn(Optional.empty());

			// expected
			assertThrows(NotExistException.class,
				() -> agendaAnnouncementAdminService.getAgendaAnnouncementList(UUID.randomUUID(), pageable));
		}
	}
}
