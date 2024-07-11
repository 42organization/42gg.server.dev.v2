package gg.agenda.api.user.agenda.service;

import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agenda.controller.request.AgendaConfirmRequestDto;
import gg.agenda.api.user.agenda.controller.request.AgendaCreateDto;
import gg.agenda.api.user.agenda.controller.request.AgendaTeamAwardDto;
import gg.agenda.api.user.agenda.controller.response.AgendaKeyResponseDto;
import gg.agenda.api.user.agenda.controller.response.AgendaResponseDto;
import gg.agenda.api.user.agenda.controller.response.AgendaSimpleResponseDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.agenda.AgendaTeam;
import gg.data.agenda.type.AgendaStatus;
import gg.data.agenda.type.AgendaTeamStatus;
import gg.data.user.User;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.AgendaTeamRepository;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaService {

	private final AgendaRepository agendaRepository;

	private final AgendaAnnouncementRepository agendaAnnouncementRepository;

	private final AgendaTeamRepository agendaTeamRepository;

	@Transactional(readOnly = true)
	public AgendaResponseDto findAgendaWithLatestAnnouncement(UUID agendaKey) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
		AgendaAnnouncement announcement = agendaAnnouncementRepository
			.findLatestByAgenda(agenda).orElse(null);
		return AgendaResponseDto.MapStruct.INSTANCE.toDto(agenda, announcement);
	}

	@Transactional(readOnly = true)
	public List<AgendaSimpleResponseDto> findCurrentAgendaList() {
		return agendaRepository.findAllByStatusIs(AgendaStatus.ON_GOING).stream()
			.sorted(Comparator.comparing(Agenda::getIsOfficial, Comparator.reverseOrder())
				.thenComparing(Agenda::getDeadline, Comparator.reverseOrder()))
			.map(AgendaSimpleResponseDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
	}

	@Transactional
	public AgendaKeyResponseDto addAgenda(AgendaCreateDto agendaCreateDto, UserDto user) {
		Agenda newAgenda = AgendaCreateDto.MapStruct.INSTANCE.toEntity(agendaCreateDto, user);
		Agenda savedAgenda = agendaRepository.save(newAgenda);
		return AgendaKeyResponseDto.builder().agendaKey(savedAgenda.getAgendaKey()).build();
	}

	@Transactional(readOnly = true)
	public List<AgendaSimpleResponseDto> findHistoryAgendaList(Pageable pageable) {
		return agendaRepository.findAllByStatusIs(pageable, AgendaStatus.CONFIRM).getContent().stream()
			.map(AgendaSimpleResponseDto.MapStruct.INSTANCE::toDto)
			.collect(Collectors.toList());
	}

	@Transactional
	public void confirmAgenda(UserDto user, UUID agendaKey, AgendaConfirmRequestDto agendaConfirmRequestDto) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
		if (!user.getIntraId().equals(agenda.getHostIntraId())) {
			throw new ForbiddenException(CONFIRM_FORBIDDEN);
		}
		if (agenda.getIsRanking()) {
			agendaConfirmRequestDto.mustNotNullOrEmpty();
		}
		agendaConfirmRequestDto.getAwards().forEach(award -> {
			AgendaTeam agendaTeam = agendaTeamRepository
				.findByAgendaAndNameAndStatus(agenda, award.getTeamName(), AgendaTeamStatus.CONFIRM)
				.orElseThrow(() -> new NotExistException(TEAM_NOT_FOUND));
			agendaTeam.acceptAward(award.getAwardName(), award.getAwardPriority());
		});
		agenda.confirm(LocalDateTime.now());
	}
}
