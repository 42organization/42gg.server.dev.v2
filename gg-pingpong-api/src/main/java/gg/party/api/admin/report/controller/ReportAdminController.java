package gg.party.api.admin.report.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.admin.report.controller.request.ReportPageReqDto;
import gg.party.api.admin.report.controller.response.CommentReportPageResDto;
import gg.party.api.admin.report.controller.response.RoomReportPageResDto;
import gg.party.api.admin.report.controller.response.UserReportPageResDto;
import gg.party.api.admin.report.service.ReportAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/admin/reports")
public class ReportAdminController {
	private final ReportAdminService reportAdminService;

	/**
	 * 댓글 신고 전체 리스트 조회
	 * return 200 status code(성공 status)
	 */
	@GetMapping("/comments")
	public ResponseEntity<CommentReportPageResDto> getCommentReports(
		@ModelAttribute @Valid ReportPageReqDto reportPageReqDto) {
		CommentReportPageResDto commentReportPageResDto = reportAdminService.getCommentReports(reportPageReqDto);
		return ResponseEntity.ok(commentReportPageResDto);
	}

	/**
	 * 방 신고 전체 리스트 조회
	 * return 200 status code(성공 status)
	 */
	@GetMapping("/rooms")
	public ResponseEntity<RoomReportPageResDto> getRoomReports(
		@ModelAttribute @Valid ReportPageReqDto reportPageReqDto) {
		RoomReportPageResDto roomReportPageResDto = reportAdminService.getRoomReports(reportPageReqDto);
		return ResponseEntity.ok(roomReportPageResDto);
	}

	/**
	 * 노쇼 신고 전체 리스트 조회
	 * return 200 status code(성공 status)
	 */
	@GetMapping("/users")
	public ResponseEntity<UserReportPageResDto> getUserReports(
		@ModelAttribute @Valid ReportPageReqDto reportPageReqDto) {
		UserReportPageResDto userReportPageResDto = reportAdminService.getUserReports(reportPageReqDto);
		return ResponseEntity.ok(userReportPageResDto);
	}
}
