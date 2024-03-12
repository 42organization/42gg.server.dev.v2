package gg.party.api.admin.report.controller.response;

import java.time.LocalDateTime;

import gg.data.party.UserReport;
import lombok.Getter;

/**
 * 노쇼 신고 dto
 */
@Getter
public class UserReportAdminResDto {
	private Long id;
	private String reporterIntraId;
	private String reporteeIntraId;
	private Long roomId;
	private String message;
	private LocalDateTime createdAt;

	public UserReportAdminResDto(UserReport userReport) {
		this.id = userReport.getId();
		this.reporterIntraId = userReport.getReporter().getIntraId();
		this.reporteeIntraId = userReport.getReportee().getIntraId();
		this.roomId = userReport.getRoom().getId();
		this.message = userReport.getMessage();
		this.createdAt = userReport.getCreatedAt();
	}
}
