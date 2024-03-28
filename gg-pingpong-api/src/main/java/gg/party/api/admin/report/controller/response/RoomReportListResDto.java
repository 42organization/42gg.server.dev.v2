package gg.party.api.admin.report.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class RoomReportListResDto {
	private List<RoomReportAdminResDto> roomReportList;
	private int totalPages;

	public RoomReportListResDto(List<RoomReportAdminResDto> roomReportList, int totalPages) {
		this.roomReportList = roomReportList;
		this.totalPages = totalPages;
	}
}
