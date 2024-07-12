package gg.agenda.api.user.agenda.service;

import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agenda.controller.request.AgendaConfirmReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaService {

	private final AgendaRepository agendaRepository;

	private final AgendaAnnouncementRepository agendaAnnouncementRepository;

	private final AgendaTeamRepository agendaTeamRepository;

	@Transactional(readOnly = true)
	public Agenda findAgendaByAgendaKey(UUID agendaKey) {
		return agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public Optional<AgendaAnnouncement> findAgendaWithLatestAnnouncement(Agenda agenda) {
		return agendaAnnouncementRepository.findLatestByAgenda(agenda);
	}

	@Transactional(readOnly = true)
	public List<Agenda> findCurrentAgendaList() {
		return agendaRepository.findAllByStatusIs(AgendaStatus.ON_GOING).stream()
			.sorted(Comparator.comparing(Agenda::getIsOfficial, Comparator.reverseOrder())
				.thenComparing(Agenda::getDeadline, Comparator.reverseOrder()))
			.collect(Collectors.toList());
	}

	@Transactional
	public Agenda addAgenda(AgendaCreateDto agendaCreateDto, UserDto user) {
		Agenda newAgenda = AgendaCreateDto.MapStruct.INSTANCE.toEntity(agendaCreateDto, user);
		return agendaRepository.save(newAgenda);
	}

	@Transactional(readOnly = true)
	public List<Agenda> findHistoryAgendaList(Pageable pageable) {
		return agendaRepository.findAllByStatusIs(pageable, AgendaStatus.CONFIRM).getContent();
	}

	@Transactional
	public void confirmAgenda(AgendaConfirmReqDto agendaConfirmReqDto, Agenda agenda) {
		if (agenda.getIsRanking()) {
			agendaConfirmReqDto.getAwards().forEach(award -> {
				AgendaTeam agendaTeam = agendaTeamRepository
					.findByAgendaAndNameAndStatus(agenda, award.getTeamName(), AgendaTeamStatus.CONFIRM)
					.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));
				agendaTeam.acceptAward(award.getAwardName(), award.getAwardPriority());
			});
		}
		agenda.confirm(LocalDateTime.now());
	}
}
