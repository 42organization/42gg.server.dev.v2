package gg.recruit.api.user.controller.response;

import java.util.List;
import java.util.stream.Collectors;

import gg.recruit.api.user.service.response.ApplicationListDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyApplicationsResDto {
	private List<ApplicationResDto> applications;

	public MyApplicationsResDto(ApplicationListDto applicationListDto) {
		applications = applicationListDto.getApplications()
			.stream()
			.map(ApplicationResDto::new)
			.collect(Collectors.toList());
	}
}
