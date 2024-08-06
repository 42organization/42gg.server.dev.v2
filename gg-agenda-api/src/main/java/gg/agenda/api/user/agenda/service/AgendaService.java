package gg.agenda.api.user.agenda.service;

import static gg.utils.exception.ErrorCode.*;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import gg.agenda.api.user.agenda.controller.request.AgendaAwardsReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateReqDto;
import gg.agenda.api.user.agenda.controller.request.AgendaTeamAward;
import gg.agenda.api.user.agendateam.service.AgendaTeamService;
import gg.agenda.api.user.ticket.service.TicketService;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamProfileRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.utils.exception.custom.BusinessException;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;
import gg.utils.file.handler.ImageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgendaService {

	private final AgendaRepository agendaRepository;

	private final AgendaTeamRepository agendaTeamRepository;

	private final AgendaTeamProfileRepository agendaTeamProfileRepository;

	private final TicketService ticketService;

  private final AgendaTeamService agendaTeamService;

	private final ImageHandler imageHandler;

	@Value("${info.image.defaultUrl}")
	private String defaultUri;

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
	public Agenda addAgenda(AgendaCreateReqDto createDto, MultipartFile agendaPoster, UserDto user) {
		try {
			URL storedUrl = imageHandler.uploadImageOrDefault(agendaPoster, createDto.getAgendaTitle(), defaultUri);
			createDto.updatePosterUri(storedUrl);
			Agenda newAgenda = AgendaCreateReqDto.MapStruct.INSTANCE.toEntity(createDto, user.getIntraId());
			return agendaRepository.save(newAgenda);
		} catch (IOException e) {
			log.error("Failed to upload image for agenda poster", e);
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
			agendaTeamService.leaveTeamAll(openTeam);
		}
		agenda.confirmAgenda();
	}
}
