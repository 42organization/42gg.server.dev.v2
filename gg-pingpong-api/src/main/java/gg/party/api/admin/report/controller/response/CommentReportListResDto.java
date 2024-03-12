package gg.party.api.admin.report.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class CommentReportListResDto {
	private List<CommentReportAdminResDto> commentReportPageList;
	private int totalPages;

	public CommentReportListResDto(List<CommentReportAdminResDto> commentReportPageList, int totalPages) {
		this.commentReportPageList = commentReportPageList;
		this.totalPages = totalPages;
	}
}
