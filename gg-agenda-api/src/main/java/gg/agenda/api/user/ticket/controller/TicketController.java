package gg.agenda.api.user.ticket.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import gg.agenda.api.user.ticket.service.TicketService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@Controller("/agenda/ticket")
@RequiredArgsConstructor
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
}
