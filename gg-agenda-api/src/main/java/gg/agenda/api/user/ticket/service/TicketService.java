package gg.agenda.api.user.ticket.service;

import static gg.utils.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gg.auth.UserDto;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.repo.agenda.AgendaProfileRepository;
import gg.repo.agenda.TicketRepository;
import gg.utils.exception.custom.DuplicationException;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {
	private final TicketRepository ticketRepository;
	private final AgendaProfileRepository agendaProfileRepository;

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
	public int findTicketCount(UserDto user) {
		AgendaProfile profile = agendaProfileRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));
		List<Ticket> tickets = ticketRepository.findByAgendaProfileIdAndIsUsedFalseAndIsApprovedTrue(profile.getId());
		return tickets.size();
	}
}
