package gg.agenda.api.user.agendaannouncement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agendaannouncement.controller.request.AgendaAnnouncementCreateReqDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.repo.agenda.AgendaAnnouncementRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaAnnouncementService {

	private final AgendaAnnouncementRepository agendaAnnouncementRepository;

	@Transactional
	public void addAgendaAnnouncement(AgendaAnnouncementCreateReqDto announceCreateDto, Agenda agenda) {
		AgendaAnnouncement newAnnounce = AgendaAnnouncementCreateReqDto
			.MapStruct.INSTANCE.toEntity(announceCreateDto, agenda);
		agendaAnnouncementRepository.save(newAnnounce);
	}

	@Transactional(readOnly = true)
	public List<AgendaAnnouncement> findAnnouncementListByAgenda(Pageable pageable, Agenda agenda) {
		return agendaAnnouncementRepository.findAllByAgendaAndIsShowIsTrue(pageable, agenda);
	}

	@Transactional(readOnly = true)
	public Optional<AgendaAnnouncement> findAgendaWithLatestAnnouncement(Agenda agenda) {
		return agendaAnnouncementRepository.findLatestByAgenda(agenda);
	}
}
