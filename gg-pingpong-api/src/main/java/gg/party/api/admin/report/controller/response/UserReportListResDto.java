package gg.party.api.admin.report.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class UserReportListResDto {
	private List<UserReportAdminResDto> userReportList;
	private int totalPages;

	public UserReportListResDto(List<UserReportAdminResDto> userReportList, int totalPages) {
		this.userReportList = userReportList;
		this.totalPages = totalPages;
	}
}
