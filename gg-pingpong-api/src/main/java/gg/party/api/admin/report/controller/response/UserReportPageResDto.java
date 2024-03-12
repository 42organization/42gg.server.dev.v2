package gg.party.api.admin.report.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class UserReportPageResDto {
	private List<UserReportListAdminResDto> userReportPageList;
	private int totalPages;

	public UserReportPageResDto(List<UserReportListAdminResDto> userReportPageList, int totalPages) {
		this.userReportPageList = userReportPageList;
		this.totalPages = totalPages;
	}
}
