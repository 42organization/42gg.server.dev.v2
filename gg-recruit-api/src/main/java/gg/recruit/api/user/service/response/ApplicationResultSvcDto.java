package gg.recruit.api.user.service.response;

import java.time.LocalDateTime;

import gg.data.recruit.application.enums.ApplicationStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ApplicationResultSvcDto {
	private String title;
	private ApplicationStatus status;
	private LocalDateTime interviewDate;

	public static ApplicationResultSvcDto nullResult() {
		return new ApplicationResultSvcDto(null, null, null);
	}

	public static ApplicationResultSvcDto of(String title, ApplicationStatus status, LocalDateTime interviewDate) {
		return new ApplicationResultSvcDto(title, status, interviewDate);
	}

}
