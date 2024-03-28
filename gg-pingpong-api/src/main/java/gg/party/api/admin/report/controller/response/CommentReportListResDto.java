package gg.party.api.admin.report.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class CommentReportListResDto {
	private List<CommentReportAdminResDto> commentReportList;
	private int totalPages;

	public CommentReportListResDto(List<CommentReportAdminResDto> commentReportList, int totalPages) {
		this.commentReportList = commentReportList;
		this.totalPages = totalPages;
	}
}
