package gg.recruit.api.user.application.service.response;

import java.util.List;
import java.util.stream.Collectors;

import gg.data.recruit.application.Application;
import lombok.Getter;

@Getter
public class ApplicationListSvcDto {
	List<ApplicationSvcDto> applications;

	public ApplicationListSvcDto(List<Application> applications) {
		this.applications = applications.stream().map(ApplicationSvcDto::new)
			.collect(Collectors.toList());
	}
}
