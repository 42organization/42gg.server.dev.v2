package gg.agenda.api.admin.ticket.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.admin.ticket.controller.request.TicketAddAdminReqDto;
import gg.agenda.api.admin.ticket.controller.response.TicketAddAdminResDto;
import gg.agenda.api.admin.ticket.service.TicketAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/admin/ticket")
public class TicketAdminController {
	private final TicketAdminService ticketAdminService;

	/**
	 * 티켓 설정 추가
	 * @param intraId 사용자 정보
	 */
	@PostMapping
	public ResponseEntity<TicketAddAdminResDto> ticketSetupAdd(@RequestParam String intraId,
		@RequestBody TicketAddAdminReqDto ticketAddAdminReqDto) {
		Long ticketId = ticketAdminService.addTicket(intraId, ticketAddAdminReqDto);
		TicketAddAdminResDto ticketAddAdminResDto = new TicketAddAdminResDto(ticketId);
		return ResponseEntity.status(HttpStatus.CREATED).body(ticketAddAdminResDto);
	}
}
