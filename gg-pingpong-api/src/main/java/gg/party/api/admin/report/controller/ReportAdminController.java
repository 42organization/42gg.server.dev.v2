package gg.party.api.admin.report.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.admin.report.controller.response.CommentReportListAdminResDto;
import gg.party.api.admin.report.controller.response.RoomReportListAdminResDto;
import gg.party.api.admin.report.controller.response.UserReportListAdminResDto;
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
	public ResponseEntity<List<CommentReportListAdminResDto>> getCommentReports() {
		List<CommentReportListAdminResDto> reports = reportAdminService.getCommentReports();
		return ResponseEntity.ok(reports);
	}

	/**
	 * 방 신고 전체 리스트 조회
	 * return 200 status code(성공 status)
	 */
	@GetMapping("/rooms")
	public ResponseEntity<List<RoomReportListAdminResDto>> getRoomReports() {
		List<RoomReportListAdminResDto> reports = reportAdminService.getRoomReports();
		return ResponseEntity.ok(reports);
	}

	/**
	 * 노쇼 신고 전체 리스트 조회
	 * return 200 status code(성공 status)
	 */
	@GetMapping("/users")
	public ResponseEntity<List<UserReportListAdminResDto>> getUserReports() {
		List<UserReportListAdminResDto> reports = reportAdminService.getUserReports();
		return ResponseEntity.ok(reports);
	}
}
