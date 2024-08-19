package gg.agenda.api.user.agendaannouncement.service;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gg.agenda.api.user.agendaannouncement.controller.request.AgendaAnnouncementCreateReqDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
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
	class CreateAgendaAnnouncement {

		@Test
		@DisplayName("AgendaAnnouncement 생성 성공")
		void createAgendaAnnouncementSuccess() {
			// given
			Agenda agenda = mock(Agenda.class);
			AgendaAnnouncement newAnnounce = mock(AgendaAnnouncement.class);
			AgendaAnnouncementCreateReqDto dto = AgendaAnnouncementCreateReqDto.builder()
				.title("title").content("content").build();
			when(agendaAnnouncementRepository.save(any())).thenReturn(newAnnounce);

			// when
			agendaAnnouncementService.addAgendaAnnouncement(dto, agenda);

			// then
			verify(agendaAnnouncementRepository, times(1)).save(any());
		}
	}

	@Nested
	@DisplayName("AgendaAnnouncement 전체 조회")
	class GetAgendaAnnouncementList {

		@Test
		@DisplayName("AgendaAnnouncement 전체 조회 성공")
		void getAgendaAnnouncementListSuccess() {
			// given
			Agenda agenda = mock(Agenda.class);
			Pageable pageable = mock(Pageable.class);
			when(agendaAnnouncementRepository.findListByAgenda(pageable, agenda))
				.thenReturn(Page.empty());

			// when
			agendaAnnouncementService.findAnnouncementListByAgenda(pageable, agenda);

			// then
			verify(agendaAnnouncementRepository, times(1)).findListByAgenda(pageable, agenda);
		}
	}

	@Nested
	@DisplayName("마지막 AgendaAnnouncement 조회")
	class GetAgendaAnnouncementLatest {

		@Test
		@DisplayName("마지막 AgendaAnnouncement 조회 성공")
		void getAgendaAnnouncementLatestSuccess() {
			// given
			Agenda agenda = mock(Agenda.class);
			AgendaAnnouncement announcement = mock(AgendaAnnouncement.class);
			when(agendaAnnouncementRepository.findLatestByAgenda(agenda)).thenReturn(Optional.of(announcement));

			// when
			agendaAnnouncementService.findLatestAnnounceTitleByAgendaOrDefault(agenda, "");

			// then
			verify(agendaAnnouncementRepository, times(1)).findLatestByAgenda(agenda);
		}
	}
}
