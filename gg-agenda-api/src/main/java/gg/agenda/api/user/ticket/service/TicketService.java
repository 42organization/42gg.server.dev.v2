package gg.agenda.api.user.ticket.service;

import static gg.utils.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gg.agenda.api.user.ticket.controller.response.TicketHistoryResDto;
import gg.auth.UserDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Auth42Token;
import gg.data.agenda.Ticket;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.AgendaRepository;
import gg.repo.agenda.Auth42TokenRedisRepository;
import gg.repo.agenda.TicketRepository;
import gg.utils.exception.custom.DuplicationException;
import gg.utils.exception.custom.ForbiddenException;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
	private final TicketRepository ticketRepository;
	private final AgendaRepository agendaRepository;
	private final AgendaProfileRepository agendaProfileRepository;
	private final Auth42TokenRedisRepository auth42TokenRedisRepository;

	/**
	 * 티켓 환불
	 * @param changedProfiles 변경된 프로필 목록
	 * @param agendaKey 아젠다 키
	 * @Annotation 트랜잭션의 원자성을 보장하기 위해 부모 트랜잭션이 없을경우 예외를 발생시키는 Propagation.MANDATORY로 설정
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public void refundTickets(List<AgendaProfile> changedProfiles, UUID agendaKey) {
		List<Ticket> tickets = new ArrayList<>();
		for (
			AgendaProfile profile : changedProfiles) {
			Ticket ticket = Ticket.createRefundedTicket(profile, agendaKey);
			tickets.add(ticket);
		}
		ticketRepository.saveAll(tickets);
	}

	/**
	 * 티켓 설정 추가
	 * @param user 사용자 정보
	 */
	@Transactional
	public void addTicketSetup(UserDto user) {
		AgendaProfile profile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
		Optional<Ticket> optionalTicket = ticketRepository.findByAgendaProfileAndIsApprovedFalse(profile);
		if (optionalTicket.isPresent()) {
			throw new DuplicationException(ALREADY_TICKET_SETUP);
		}
		ticketRepository.save(Ticket.createNotApporveTicket(profile));
	}

	/**
	 * 티켓 수 조회
	 * @param user 사용자 정보
	 * @return 티켓 수
	 */
	@Transactional(readOnly = true)
	public int findTicketCount(UserDto user) {
		AgendaProfile profile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
		List<Ticket> tickets = ticketRepository.findByAgendaProfileIdAndIsUsedFalseAndIsApprovedTrue(profile.getId());
		return tickets.size();
	}

	/**
	 * 티켓 승인/거절
	 * @param user 사용자 정보
	 */
	@Transactional
	public void modifyTicketApprove(UserDto user) {
		AgendaProfile profile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
		Ticket ticket = ticketRepository.findByAgendaProfileAndIsApprovedFalse(profile)
			.orElseThrow(() -> new NotExistException(NOT_SETUP_TICKET));
		if (ticket.getModifiedAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
			throw new ForbiddenException(TICKET_FORBIDDEN);
		}
		Optional<Auth42Token> auth42Token = auth42TokenRedisRepository.findByIntraId(user.getIntraId());
		if (auth42Token.isPresent()) {
			throw new NotExistException(AUTH_NOT_FOUND);
		}
		ticket.changeIsApproved();
		ticketRepository.save(ticket);
	}

	/**
	 * 티켓 이력 조회
	 * @param user 사용자 정보
	 * @param pageable 페이지 정보
	 * @return 티켓 이력 목록
	 */
	@Transactional(readOnly = true)
	public List<TicketHistoryResDto> listTicketHistory(UserDto user, Pageable pageable) {
		AgendaProfile profile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		Page<Ticket> tickets = ticketRepository.findByAgendaProfileId(profile.getId(), pageable);

		List<TicketHistoryResDto> ticketHistoryResDtos = tickets.getContent().stream()
			.map(TicketHistoryResDto::new)
			.collect(Collectors.toList());

		for (TicketHistoryResDto dto : ticketHistoryResDtos) {
			if (dto.getIssuedFromKey() != null) {
				Agenda agenda = agendaRepository.findAgendaByAgendaKey(dto.getIssuedFromKey()).orElse(null);
				dto.changeIssuedFrom(agenda);
			}
			if (dto.getUsedToKey() != null) {
				Agenda agenda = agendaRepository.findAgendaByAgendaKey(dto.getUsedToKey()).orElse(null);
				dto.changeUsedTo(agenda);
			}
		}
		return ticketHistoryResDtos;
	}
}
