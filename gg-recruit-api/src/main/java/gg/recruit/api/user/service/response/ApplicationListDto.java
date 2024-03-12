package gg.recruit.api.user.service.response;

import java.util.List;
import java.util.stream.Collectors;

import gg.data.recruit.application.Application;
import lombok.Getter;

@Getter
public class ApplicationListDto {
	List<ApplicationDto> applications;

	public ApplicationListDto(List<Application> applications) {
		this.applications = applications.stream().map(ApplicationDto::new)
			.collect(Collectors.toList());
	}
}
