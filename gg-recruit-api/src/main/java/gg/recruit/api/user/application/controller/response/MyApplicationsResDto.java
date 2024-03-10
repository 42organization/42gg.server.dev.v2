package gg.recruit.api.user.application.controller.response;

import java.util.List;
import java.util.stream.Collectors;

import gg.recruit.api.user.application.service.response.ApplicationListSvcDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyApplicationsResDto {
	private List<ApplicationResDto> applications;

	public MyApplicationsResDto(ApplicationListSvcDto applicationListDto) {
		applications = applicationListDto.getApplications()
			.stream()
			.map(ApplicationResDto::new)
			.collect(Collectors.toList());
	}
}
