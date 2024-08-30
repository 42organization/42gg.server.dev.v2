package gg.agenda.api.user.ticket.controller;

import static gg.utils.exception.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agenda.service.AgendaService;
import gg.agenda.api.user.agendaprofile.service.AgendaProfileFindService;
import gg.agenda.api.user.ticket.controller.response.TicketCountResDto;
import gg.agenda.api.user.ticket.controller.response.TicketHistoryResDto;
import gg.agenda.api.user.ticket.service.TicketService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.utils.cookie.CookieUtil;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;
import gg.utils.exception.custom.AuthenticationException;
import gg.utils.exception.user.TokenNotValidException;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/ticket")
public class TicketController {
	private final CookieUtil cookieUtil;
	private final TicketService ticketService;
	private final AgendaProfileFindService agendaProfileFindService;
	private final AgendaService agendaService;

	/**
	 * 티켓 설정 추가
	 * @param user 사용자 정보
	 */
	@PostMapping
	public ResponseEntity<Void> ticketSetupAdd(@Parameter(hidden = true) @Login UserDto user) {
		ticketService.addTicketSetup(user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 티켓 수 조회
	 * boolean setupTicket = tickets.size() > approvedCount; setupTicket이 있는지 확인하는 부분
	 * @param user 사용자 정보
	 */
	@GetMapping
	public ResponseEntity<TicketCountResDto> ticketCountFind(@Parameter(hidden = true) @Login UserDto user) {
		AgendaProfile profile = agendaProfileFindService.findAgendaProfileByIntraId(user.getIntraId());
		List<Ticket> tickets = ticketService.findTicketList(profile);
		long approvedCount = tickets.stream()
			.filter(Ticket::getIsApproved)
			.count();
		boolean setupTicket = tickets.size() > approvedCount;

		return ResponseEntity.ok(new TicketCountResDto(tickets.size(), setupTicket));
	}

	/**
	 * 티켓 승인/거절
	 * @param user 사용자 정보
	 */
	@PatchMapping
	public ResponseEntity<Void> ticketApproveModify(@Parameter(hidden = true) @Login UserDto user,
		HttpServletResponse response) {
		try {
			AgendaProfile profile = agendaProfileFindService.findAgendaProfileByIntraId(user.getIntraId());
			ticketService.modifyTicketApprove(profile);
			return ResponseEntity.noContent().build();
		} catch (TokenNotValidException e) {
			cookieUtil.deleteCookie(response, "refresh_token");
			throw new AuthenticationException(REFRESH_TOKEN_EXPIRED);
		}
	}

	/**
	 * 티켓 이력 조회
	 * @param user 사용자 정보
	 * @param pageRequest 페이지 정보
	 */
	@GetMapping("/history")
	public ResponseEntity<PageResponseDto<TicketHistoryResDto>> ticketHistoryList(
		@Parameter(hidden = true) @Login UserDto user, @ModelAttribute @Valid PageRequestDto pageRequest) {
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		Page<Ticket> tickets = ticketService.findTicketsByUserId(user.getId(), pageable);

		List<TicketHistoryResDto> ticketDtos = tickets.stream()
			.map(ticket -> {
				TicketHistoryResDto dto = new TicketHistoryResDto(ticket);

				if (dto.getIssuedFromKey() != null) {
					Agenda agendaIssuedFrom = agendaService.getAgenda(dto.getIssuedFromKey()).orElse(null);
					dto.changeIssuedFrom(agendaIssuedFrom);
				}

				if (dto.getUsedToKey() != null) {
					Agenda agendaUsedTo = agendaService.getAgenda(dto.getUsedToKey()).orElse(null);
					dto.changeUsedTo(agendaUsedTo);
				}

				return dto;
			})
			.collect(Collectors.toList());

		PageResponseDto<TicketHistoryResDto> pageResponseDto = PageResponseDto.of(
			tickets.getTotalElements(), ticketDtos);
		return ResponseEntity.ok(pageResponseDto);
	}
}
