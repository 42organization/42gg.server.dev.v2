package gg.party.api.admin.report.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class UserReportListResDto {
	private List<UserReportAdminResDto> userReportPageList;
	private int totalPages;

	public UserReportListResDto(List<UserReportAdminResDto> userReportPageList, int totalPages) {
		this.userReportPageList = userReportPageList;
		this.totalPages = totalPages;
	}
}
