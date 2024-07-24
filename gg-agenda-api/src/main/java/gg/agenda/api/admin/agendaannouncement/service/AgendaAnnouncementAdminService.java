package gg.agenda.api.admin.agendaannouncement.service;

import static gg.utils.exception.ErrorCode.*;

import gg.agenda.api.admin.agendaannouncement.controller.request.AgendaAnnouncementAdminUpdateReqDto;
import gg.agenda.api.user.agendaannouncement.controller.request.AgendaAnnouncementCreateReqDto;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaAnnouncementAdminRepository;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaAnnouncementAdminService {

	private final AgendaAdminRepository agendaAdminRepository;

	private final AgendaAnnouncementAdminRepository agendaAnnouncementAdminRepository;

	@Transactional(readOnly = true)
	public List<AgendaAnnouncement> getAgendaAnnouncementList(UUID agendaKey, Pageable pageable) {
		Agenda agenda = agendaAdminRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
		return agendaAnnouncementAdminRepository.findAllByAgenda(agenda, pageable).getContent();
	}

	@Transactional
	public void updateAgendaAnnouncement(AgendaAnnouncementAdminUpdateReqDto updateReqDto) {
		AgendaAnnouncement announcement = agendaAnnouncementAdminRepository.findById(updateReqDto.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_ANNOUNCEMENT_NOT_FOUND));
		announcement.update(updateReqDto.getTitle(), updateReqDto.getContent(), updateReqDto.getIsShow());
	}
}
