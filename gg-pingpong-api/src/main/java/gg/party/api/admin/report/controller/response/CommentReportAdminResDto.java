package gg.party.api.admin.report.controller.response;

import java.time.LocalDateTime;

import gg.data.party.CommentReport;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CommentReportAdminResDto {
	private Long id;
	private String reporterIntraId;
	private Long commentId;
	private Long roomId;
	private String message;
	private LocalDateTime createdAt;

	public CommentReportAdminResDto(CommentReport commentReport) {
		this.id = commentReport.getId();
		this.reporterIntraId = commentReport.getReporter().getIntraId();
		this.commentId = commentReport.getComment().getId();
		this.roomId = commentReport.getRoom().getId();
		this.message = commentReport.getMessage();
		this.createdAt = commentReport.getCreatedAt();
	}
}
