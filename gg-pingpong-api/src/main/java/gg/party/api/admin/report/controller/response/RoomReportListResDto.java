package gg.party.api.admin.report.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class RoomReportListResDto {
	private List<RoomReportAdminResDto> roomReportPageList;
	private int totalPages;

	public RoomReportListResDto(List<RoomReportAdminResDto> roomReportPageList, int totalPages) {
		this.roomReportPageList = roomReportPageList;
		this.totalPages = totalPages;
	}
}
