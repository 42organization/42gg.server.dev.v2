package gg.agenda.api.user.agenda.service;

import static gg.utils.exception.ErrorCode.*;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.agenda.controller.dto.AgendaResponseDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaAnnouncement;
import gg.repo.agenda.AgendaAnnouncementRepository;
import gg.repo.agenda.AgendaRepository;
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
}
