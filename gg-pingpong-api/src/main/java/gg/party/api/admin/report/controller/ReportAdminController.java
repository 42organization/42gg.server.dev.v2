package gg.party.api.admin.report.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.admin.report.controller.request.ReportPageReqDto;
import gg.party.api.admin.report.controller.response.CommentReportListResDto;
import gg.party.api.admin.report.controller.response.RoomReportListResDto;
import gg.party.api.admin.report.controller.response.UserReportListResDto;
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
	public ResponseEntity<CommentReportListResDto> getCommentReports(
		@ModelAttribute @Valid ReportPageReqDto reportPageReqDto) {
		CommentReportListResDto commentReportListResDto = reportAdminService.getCommentReports(reportPageReqDto);
		return ResponseEntity.ok(commentReportListResDto);
	}

	/**
	 * 방 신고 전체 리스트 조회
	 * return 200 status code(성공 status)
	 */
	@GetMapping("/rooms")
	public ResponseEntity<RoomReportListResDto> getRoomReports(
		@ModelAttribute @Valid ReportPageReqDto reportPageReqDto) {
		RoomReportListResDto roomReportListResDto = reportAdminService.getRoomReports(reportPageReqDto);
		return ResponseEntity.ok(roomReportListResDto);
	}

	/**
	 * 노쇼 신고 전체 리스트 조회
	 * return 200 status code(성공 status)
	 */
	@GetMapping("/users")
	public ResponseEntity<UserReportListResDto> getUserReports(
		@ModelAttribute @Valid ReportPageReqDto reportPageReqDto) {
		UserReportListResDto userReportListResDto = reportAdminService.getUserReports(reportPageReqDto);
		return ResponseEntity.ok(userReportListResDto);
	}
}
