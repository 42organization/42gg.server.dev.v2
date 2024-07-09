package gg.agenda.api.user.agenda.service;

import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agenda.controller.dto.AgendaCreateDto;
import gg.agenda.api.user.agenda.controller.dto.AgendaKeyResponseDto;
import gg.agenda.api.user.agenda.controller.dto.AgendaResponseDto;
import gg.agenda.api.user.agenda.controller.dto.AgendaSimpleResponseDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.data.agenda.type.AgendaStatus;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.repo.agenda.AgendaRepository;
import gg.utils.dto.PageRequestDto;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaService {

	private final AgendaRepository agendaRepository;

	private final AgendaAnnouncementRepository agendaAnnouncementRepository;

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
	public void confirmAgenda(UUID agendaKey) {
		Agenda agenda = agendaRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
		agenda.confirm(LocalDateTime.now());
	}
}
