package gg.party.api.user.report.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.party.api.user.report.request.ReportRoomReqDto;
import gg.party.api.user.report.service.ReportService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/reports")
public class ReportController {
	private final ReportService reportService;

	/**
	 * 방을 신고한다.
	 * * @param reqDto 신고 내용
	 * * @param roomId 방 번호
	 * @return 참여중인 방 전체 List
	 */
	@PostMapping("/rooms/{room_id}")
	public ResponseEntity<Long> reportRoom(@PathVariable("room_id") Long roomId,
		@RequestBody @Valid ReportRoomReqDto reportRoomReqDto,
		@Parameter(hidden = true) @Login UserDto user) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(reportService.addReportRoom(roomId, reportRoomReqDto, user));
	}
}
