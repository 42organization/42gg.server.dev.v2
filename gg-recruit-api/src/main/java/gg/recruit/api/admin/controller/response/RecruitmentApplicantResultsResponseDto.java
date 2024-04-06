package gg.recruit.api.admin.controller.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RecruitmentApplicantResultsResponseDto {
	private List<RecruitmentApplicantResultResponseDto> applicationResults;

	public RecruitmentApplicantResultsResponseDto(List<RecruitmentApplicantResultResponseDto> applicationResults) {
		this.applicationResults = applicationResults;
	}
}
