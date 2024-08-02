package gg.agenda.api.user.agenda.service;

import static gg.utils.exception.ErrorCode.*;

import gg.utils.exception.custom.BusinessException;
import gg.utils.file.handler.ImageHandler;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agenda.controller.request.AgendaAwardsReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaTeamAward;
import gg.agenda.api.user.ticket.service.TicketService;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.AgendaTeamProfile;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgendaService {

	private final AgendaRepository agendaRepository;

	private final AgendaTeamRepository agendaTeamRepository;

	private final AgendaTeamProfileRepository agendaTeamProfileRepository;

	private final TicketService ticketService;

	private final ImageHandler imageHandler;

	@Transactional(readOnly = true)
	public Agenda findAgendaByAgendaKey(UUID agendaKey) {
		return agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public List<Agenda> findCurrentAgendaList() {
		return agendaRepository.findAllByStatusIs(AgendaStatus.OPEN).stream()
			.sorted(agendaComparatorWithIsOfficialThenDeadline())
			.collect(Collectors.toList());
	}

	private Comparator<Agenda> agendaComparatorWithIsOfficialThenDeadline() {
		return Comparator.comparing(Agenda::getIsOfficial, Comparator.reverseOrder())
			.thenComparing(Agenda::getDeadline, Comparator.reverseOrder());
	}

	@Transactional
	public Agenda addAgenda(AgendaCreateReqDto agendaCreateReqDto, UserDto user) {
		try {
			URL savedUrl = null;
			if (Objects.nonNull(agendaCreateReqDto.getAgendaPoster())) {
				savedUrl = imageHandler.uploadImage(agendaCreateReqDto.getAgendaPoster(), user.getIntraId());
			}
			Agenda newAgenda = AgendaCreateReqDto.MapStruct.INSTANCE.toEntity(
				agendaCreateReqDto, user.toString(), savedUrl == null ? "" : savedUrl.toString());
			return agendaRepository.save(newAgenda);
		} catch (Exception e) {
			log.debug("Agenda add failed: {}", e.getMessage());
			throw new BusinessException(AGENDA_CREATE_FAILED);
		}
	}

	@Transactional(readOnly = true)
	public List<Agenda> findHistoryAgendaList(Pageable pageable) {
		return agendaRepository.findAllByStatusIs(AgendaStatus.FINISH, pageable).getContent();
	}

	@Transactional
	public void finishAgenda(Agenda agenda) {
		agenda.finishAgenda();
	}

	@Transactional
	public void awardAgenda(AgendaAwardsReqDto agendaAwardsReqDto, Agenda agenda) {
		List<AgendaTeam> teams = agendaTeamRepository.findAllByAgendaAndStatus(agenda, AgendaTeamStatus.CONFIRM);
		for (AgendaTeamAward agendaTeamAward : agendaAwardsReqDto.getAwards()) {
			AgendaTeam matchedTeam = teams.stream()
				.filter(team -> team.getName().equals(agendaTeamAward.getTeamName()))
				.findFirst()
				.orElseThrow(() -> new NotExistException(AGENDA_TEAM_NOT_FOUND));
			matchedTeam.acceptAward(agendaTeamAward.getAwardName(), agendaTeamAward.getAwardPriority());
		}
	}

	@Transactional
	public void confirmAgendaAndRefundTicketForOpenTeam(Agenda agenda) {
		if (agenda.getCurrentTeam() < agenda.getMinTeam()) {
			throw new ForbiddenException("팀이 모두 구성되지 않았습니다.");
		}

		List<AgendaTeam> openTeams = agendaTeamRepository.findAllByAgendaAndStatus(agenda, AgendaTeamStatus.OPEN);
		for (AgendaTeam openTeam : openTeams) {
			// TODO: AgendaTeamService의 cancelTeam 메서드를 호출하는 것이 더 좋을 수도 있음
			List<AgendaProfile> participants = agendaTeamProfileRepository
				.findAllByAgendaTeamWithFetchProfile(openTeam).stream()
				.map(AgendaTeamProfile::getProfile)
				.collect(Collectors.toList());
			ticketService.refundTickets(participants, agenda.getAgendaKey());
			openTeam.cancelTeam();
		}
		agenda.confirmAgenda();
	}
}
