package gg.agenda.api.admin.ticket.service;

import static gg.utils.exception.ErrorCode.*;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaProfileAdminRepository;
import gg.admin.repo.agenda.TicketAdminRepository;
import gg.agenda.api.admin.ticket.controller.request.TicketAddAdminReqDto;
import gg.agenda.api.admin.ticket.controller.request.TicketChangeAdminReqDto;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketAdminService {

	private final TicketAdminRepository ticketAdminRepository;
	private final AgendaProfileAdminRepository agendaProfileAdminRepository;
	private final AgendaAdminRepository agendaAdminRepository;

	/**
	 * 티켓 설정 추가
	 * @param intraId 사용자 정보
	 */
	@Transactional
	public Long addTicket(String intraId, TicketAddAdminReqDto ticketAddAdminReqDto) {
		AgendaProfile profile = agendaProfileAdminRepository.findByIntraId(intraId)
			.orElseThrow(() -> new NotExistException(AGENDA_PROFILE_NOT_FOUND));

		UUID issuedFromKey = ticketAddAdminReqDto.getIssuedFromKey();

		if (isRefundedTicket(issuedFromKey)) {
			boolean result = agendaAdminRepository.existsByAgendaKey(issuedFromKey);
			if (!result) {
				throw new NotExistException(AGENDA_NOT_FOUND);
			}
		}

		Ticket ticket = Ticket.createAdminTicket(profile, issuedFromKey);
		return ticketAdminRepository.save(ticket).getId();
	}

	private boolean isRefundedTicket(UUID issuedFromKey) {
		return Objects.nonNull(issuedFromKey);
	}

	/**
	 * AgendaProfile 변경 메서드
	 * @param ticketId 로그인한 유저의 id
	 * @param reqDto 변경할 프로필 정보
	 */
	@Transactional
	public void modifyTicket(Long ticketId, TicketChangeAdminReqDto reqDto) {
		Ticket ticket = ticketAdminRepository.findById(ticketId)
			.orElseThrow(() -> new NotExistException(TICKET_NOT_FOUND));

		UUID issuedFromKey = reqDto.getIssuedFromKey();
		if (Objects.nonNull(issuedFromKey) && !agendaAdminRepository.existsByAgendaKey(issuedFromKey)) {
			throw new NotExistException(AGENDA_NOT_FOUND);
		}

		UUID usedToKey = reqDto.getUsedToKey();
		if (Objects.nonNull(usedToKey) && !agendaAdminRepository.existsByAgendaKey(usedToKey)) {
			throw new NotExistException(AGENDA_NOT_FOUND);
		}

		ticket.updateTicketAdmin(reqDto.getIssuedFromKey(), reqDto.getUsedToKey(),
			reqDto.getIsApproved(), reqDto.getApprovedAt(), reqDto.getIsUsed(), reqDto.getUsedAt());
		ticketAdminRepository.save(ticket);
	}
}
