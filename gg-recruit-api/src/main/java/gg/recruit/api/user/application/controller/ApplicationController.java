package gg.recruit.api.user.application.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.recruit.api.user.application.controller.response.MyApplicationDetailResDto;
import gg.recruit.api.user.application.controller.response.MyApplicationsResDto;
import gg.recruit.api.user.application.service.ApplicationService;
import gg.recruit.api.user.application.service.param.FindApplicationDetailParam;
import gg.recruit.api.user.application.service.response.ApplicationListSvcDto;
import gg.recruit.api.user.application.service.response.ApplicationWithAnswerSvcDto;
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
	public MyApplicationDetailResDto getMyApplication(@Login UserDto userDto, Long recruitmentId, Long applicationId) {
		ApplicationWithAnswerSvcDto res = applicationService
			.findMyApplicationDetail(new FindApplicationDetailParam(userDto.getId(), recruitmentId, applicationId));
		return new MyApplicationDetailResDto(res);
	}

}
