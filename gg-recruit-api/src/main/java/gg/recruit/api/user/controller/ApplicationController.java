package gg.recruit.api.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.recruit.api.user.controller.response.ApplicationResultResDto;
import gg.recruit.api.user.controller.response.MyApplicationDetailResDto;
import gg.recruit.api.user.controller.response.MyApplicationsResDto;
import gg.recruit.api.user.service.ApplicationService;
import gg.recruit.api.user.service.param.FindApplicationResultParam;
import gg.recruit.api.user.service.param.FindApplicationDetailParam;
import gg.recruit.api.user.service.response.ApplicationListSvcDto;
import gg.recruit.api.user.service.response.ApplicationResultSvcDto;
import gg.recruit.api.user.service.response.ApplicationWithAnswerSvcDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitments")
public class ApplicationController {

	private final ApplicationService applicationService;

	@GetMapping("/applications")
	public MyApplicationsResDto getMyApplications(@Login UserDto userDto) {
		ApplicationListSvcDto res = applicationService.findMyApplications(userDto.getId());
		return new MyApplicationsResDto(res);
	}

	@GetMapping("{recruitmentId}/applications/{applicationId}")
	public MyApplicationDetailResDto getMyApplication(@Login @Parameter(hidden = true) UserDto userDto,
		Long recruitmentId, Long applicationId) {
		ApplicationWithAnswerSvcDto res = applicationService
			.findMyApplicationDetail(new FindApplicationDetailParam(userDto.getId(), recruitmentId, applicationId));
		return new MyApplicationDetailResDto(res);
	}

	@GetMapping("{recruitmentId}/applications/{applicationId}/result")
	public ApplicationResultResDto getApplicationResult(@Login @Parameter(hidden = true) UserDto userDto,
		Long recruitmentId, Long applicationId) {
		ApplicationResultSvcDto res = applicationService.findApplicationResult(
			new FindApplicationResultParam(userDto.getId(),
				recruitmentId, applicationId));
		return new ApplicationResultResDto(res);
	}
}
