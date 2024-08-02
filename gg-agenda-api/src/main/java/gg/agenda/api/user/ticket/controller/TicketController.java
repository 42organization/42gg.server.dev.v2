package gg.agenda.api.user.ticket.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.ticket.controller.response.TicketCountResDto;
import gg.agenda.api.user.ticket.controller.response.TicketHistoryResDto;
import gg.agenda.api.user.ticket.service.TicketService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.utils.dto.PageRequestDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/ticket")
public class TicketController {
	private final TicketService ticketService;

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
	 * @param user 사용자 정보
	 */
	@GetMapping
	public ResponseEntity<TicketCountResDto> ticketCountFind(@Parameter(hidden = true) @Login UserDto user) {
		int ticketCount = ticketService.findTicketCount(user);
		return ResponseEntity.ok().body(new TicketCountResDto(ticketCount));
	}

	@GetMapping("/history")
	public ResponseEntity<List<TicketHistoryResDto>> ticketHistoryList(@Parameter(hidden = true) @Login UserDto user,
		@RequestParam @Valid PageRequestDto pageRequest) {
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
		List<TicketHistoryResDto> tickets = ticketService.listTicketHistory(user, pageable);
		return ResponseEntity.ok().body(tickets);
	}
}
