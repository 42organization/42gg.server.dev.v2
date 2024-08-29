package gg.agenda.api.admin.ticket.controller;

import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.admin.ticket.controller.request.TicketAddAdminReqDto;
import gg.agenda.api.admin.ticket.controller.request.TicketChangeAdminReqDto;
import gg.agenda.api.admin.ticket.controller.response.TicketAddAdminResDto;
import gg.agenda.api.admin.ticket.controller.response.TicketListResDto;
import gg.agenda.api.admin.ticket.service.TicketAdminFindService;
import gg.agenda.api.admin.ticket.service.TicketAdminService;
import gg.data.agenda.Ticket;
import gg.utils.dto.PageRequestDto;
import gg.utils.dto.PageResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/admin/ticket")
public class TicketAdminController {
	private final TicketAdminService ticketAdminService;
	private final TicketAdminFindService ticketAdminFindService;

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

	/**
	 * 티켓 목록 조회하는 메서드
	 * @param pageRequest 페이지네이션 요청 정보
	 */
	@GetMapping("/list/{intraId}")
	public ResponseEntity<PageResponseDto<TicketListResDto>> getTicketList(
		@PathVariable String intraId, @ModelAttribute @Valid PageRequestDto pageRequest) {
		int page = pageRequest.getPage();
		int size = pageRequest.getSize();
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

		Page<Ticket> ticketList = ticketAdminFindService.findTicket(intraId, pageable);
		List<TicketListResDto> ticketListResDto = ticketList.stream()
			.map(ticketAdminFindService::convertAgendaKeyToTitleWhereIssuedFromAndUsedTo)
			.collect(Collectors.toList());

		PageResponseDto<TicketListResDto> pageResponseDto = PageResponseDto.of(
			ticketList.getTotalElements(), ticketListResDto);
		return ResponseEntity.ok(pageResponseDto);
	}
}
