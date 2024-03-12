package gg.party.api.admin.report.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class RoomReportPageResDto {
	private List<RoomReportListAdminResDto> roomReportPageList;
	private int totalPages;

	public RoomReportPageResDto(List<RoomReportListAdminResDto> roomReportPageList, int totalPages) {
		this.roomReportPageList = roomReportPageList;
		this.totalPages = totalPages;
	}
}
