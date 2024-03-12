package gg.party.api.admin.report.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class CommentReportPageResDto {
	private List<CommentReportListAdminResDto> commentReportPageList;
	private int totalPages;

	public CommentReportPageResDto(List<CommentReportListAdminResDto> commentReportPageList, int totalPages) {
		this.commentReportPageList = commentReportPageList;
		this.totalPages = totalPages;
	}
}
