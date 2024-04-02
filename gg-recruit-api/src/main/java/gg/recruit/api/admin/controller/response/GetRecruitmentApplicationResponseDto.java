package gg.recruit.api.admin.controller.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetRecruitmentApplicationResponseDto {
	private int page;
	private boolean isLast;
	private List<GetRecruitmentApplicationDto> applicationResults;

	public GetRecruitmentApplicationResponseDto(int page, boolean isLast,
		List<GetRecruitmentApplicationDto> applicationResults) {
		this.page = page;
		this.isLast = isLast;
		this.applicationResults = applicationResults;
	}
}
