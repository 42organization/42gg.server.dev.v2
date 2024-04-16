package gg.party.api.admin.report.controller.response;

import java.time.LocalDateTime;

import gg.data.party.RoomReport;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class RoomReportAdminResDto {
	private Long id;
	private String reporterIntraId;
	private String reporteeIntraId;
	private Long roomId;
	private String message;
	private LocalDateTime createdAt;

	public RoomReportAdminResDto(RoomReport roomReport) {
		this.id = roomReport.getId();
		this.reporterIntraId = roomReport.getReporter().getIntraId();
		this.reporteeIntraId = roomReport.getReportee().getIntraId();
		this.roomId = roomReport.getRoom().getId();
		this.message = roomReport.getMessage();
		this.createdAt = roomReport.getCreatedAt();
	}
}
