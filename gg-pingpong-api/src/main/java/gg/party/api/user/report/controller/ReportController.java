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
import gg.party.api.user.report.request.ReportReqDto;
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
	 * @param reportReqDto 신고 내용
	 * @param roomId 방 번호
	 */
	@PostMapping("/rooms/{room_id}")
	public ResponseEntity<Long> reportRoomAdd(@PathVariable("room_id") Long roomId,
		@RequestBody @Valid ReportReqDto reportReqDto,
		@Parameter(hidden = true) @Login UserDto user) {
		reportService.addReportRoom(roomId, reportReqDto, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 댓글을 신고한다.
	 * @param reportReqDto 신고 내용
	 * @param commentId 댓글 번호
	 */
	@PostMapping("/comments/{comment_id}")
	public ResponseEntity<Void> reportCommentAdd(@PathVariable("comment_id") Long commentId,
		@RequestBody @Valid ReportReqDto reportReqDto,
		@Parameter(hidden = true) @Login UserDto user) {
		reportService.addReportComment(commentId, reportReqDto, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 유저 노쇼 신고한다.
	 * @param reportReqDto 신고 내용
	 * @param roomId 방 번호
	 * @param userIntraId 유저 인트라 아이디
	 */
	@PostMapping("/rooms/{room_id}/users/{user_intra_id}")
	public ResponseEntity<Void> reportUserAdd(@PathVariable("room_id") Long roomId,
		@PathVariable("user_intra_id") String userIntraId,
		@RequestBody @Valid ReportReqDto reportReqDto,
		@Parameter(hidden = true) @Login UserDto user) {
		reportService.addReportUser(roomId, reportReqDto, userIntraId, user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
