package gg.agenda.api.admin.ticket.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.admin.ticket.controller.request.TicketAddAdminReqDto;
import gg.agenda.api.admin.ticket.controller.request.TicketChangeAdminReqDto;
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
	public ResponseEntity<TicketAddAdminResDto> ticketAdminAdd(@RequestParam String intraId,
		@RequestBody TicketAddAdminReqDto ticketAddAdminReqDto) {
		Long ticketId = ticketAdminService.addTicket(intraId, ticketAddAdminReqDto);
		TicketAddAdminResDto ticketAddAdminResDto = new TicketAddAdminResDto(ticketId);
		return ResponseEntity.status(HttpStatus.CREATED).body(ticketAddAdminResDto);
	}

	/**
	 * 티켓 변경(관리자) API
	 *
	 * @param ticketId 수정할  ticketId
	 * @param ticketChangeAdminReqDto  변경할 프로필 정보
	 * @return HTTP 상태 코드와 빈 응답
	 */
	@PatchMapping
	public ResponseEntity<String> agendaProfileModify(
		@RequestParam Long ticketId,
		@RequestBody @Valid TicketChangeAdminReqDto ticketChangeAdminReqDto) {
		ticketAdminService.modifyTicket(ticketId, ticketChangeAdminReqDto);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
