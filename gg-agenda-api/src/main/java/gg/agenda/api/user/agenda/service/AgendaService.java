package gg.agenda.api.user.agenda.service;

import static gg.utils.exception.ErrorCode.*;

import gg.agenda.api.user.agenda.controller.request.AgendaAward;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agenda.controller.request.AgendaConfirmReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateReqDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaService {

	private final AgendaRepository agendaRepository;

	private final AgendaTeamRepository agendaTeamRepository;

	@Transactional(readOnly = true)
	public Agenda findAgendaByAgendaKey(UUID agendaKey) {
		return agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public List<Agenda> findCurrentAgendaList() {
		return agendaRepository.findAllByStatusIs(AgendaStatus.OPEN).stream()
			.sorted(Comparator.comparing(Agenda::getIsOfficial, Comparator.reverseOrder())
				.thenComparing(Agenda::getDeadline, Comparator.reverseOrder()))
			.collect(Collectors.toList());
	}

	@Transactional
	public Agenda addAgenda(AgendaCreateReqDto agendaCreateReqDto, UserDto user) {
		Agenda newAgenda = AgendaCreateReqDto.MapStruct.INSTANCE.toEntity(agendaCreateReqDto, user);
		return agendaRepository.save(newAgenda);
	}

	@Transactional(readOnly = true)
	public List<Agenda> findHistoryAgendaList(Pageable pageable) {
		return agendaRepository.findAllByStatusIs(pageable, AgendaStatus.FINISH).getContent();
	}

	@Transactional
	public void finishAgendaWithAwards(AgendaConfirmReqDto agendaConfirmReqDto, Agenda agenda) {
		if (!agenda.getIsRanking()) {
			agenda.finish();
			return;
		}
		Map<String, AgendaAward> awards = AgendaConfirmReqDto.toMap(agendaConfirmReqDto.getAwards());
		List<AgendaTeam> confirmedTeams = agendaTeamRepository
			.findAllByAgendaAndStatus(agenda, AgendaTeamStatus.CONFIRM);
		for (AgendaTeam agendaTeam : confirmedTeams) {
			if (!awards.containsKey(agendaTeam.getName())) {
				continue;
			}
			AgendaAward agendaAward = awards.get(agendaTeam.getName());
			agendaTeam.acceptAward(agendaAward.getAwardName(), agendaAward.getAwardPriority());
		}
		agenda.finish();
	}

	@Transactional
	public void confirmAgenda(Agenda agenda) {
		agendaTeamRepository.findAllByAgendaAndStatus(agenda, AgendaTeamStatus.OPEN)
			.forEach(AgendaTeam::confirm);
		agenda.confirm();
	}
}
