package gg.recruit.api.admin.controller.request;

import java.time.LocalDateTime;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import gg.data.recruit.application.enums.ApplicationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class InterviewRequestDto {
	public static final String MUST_DOCS_RESULT_STATUS = "PROGRESS_INTERVIEW or INTERVIEW_FAIL 중 하나를 선택해주세요.";

	@NotNull(message = MUST_DOCS_RESULT_STATUS)
	private ApplicationStatus status;

	@NotNull
	@FutureOrPresent(message = "면접 일자는 현재 이후여야 합니다.")
	private LocalDateTime interviewDate;

	@AssertTrue(message = MUST_DOCS_RESULT_STATUS)
	private boolean isValidDocsResultStatus() {
		if (status == null) {
			return false;
		}
		if (status != ApplicationStatus.PROGRESS_INTERVIEW && status != ApplicationStatus.INTERVIEW_FAIL) {
			return false;
		}
		if (status == ApplicationStatus.PROGRESS_INTERVIEW) {
			return interviewDate != null;
		}
		return true;
	}

	public InterviewRequestDto(ApplicationStatus status, LocalDateTime interviewDate) {
		this.status = status;
		this.interviewDate = interviewDate;
	}
}
