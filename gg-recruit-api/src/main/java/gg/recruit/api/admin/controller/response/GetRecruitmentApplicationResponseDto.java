package gg.recruit.api.admin.controller.response;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import gg.data.recruit.application.Application;
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

	public static GetRecruitmentApplicationResponseDto applicationsPageToDto(Page<Application> applicationsPage) {
		int page = applicationsPage.getPageable().getPageNumber() + 1;
		boolean isLast = applicationsPage.isLast();
		List<GetRecruitmentApplicationDto> dto = applicationsPage.getContent()
			.stream()
			.map(GetRecruitmentApplicationDto.MapStruct.INSTANCE::entityToDto)
			.collect(Collectors.toList());
		return new GetRecruitmentApplicationResponseDto(page, isLast, dto);
	}
}
