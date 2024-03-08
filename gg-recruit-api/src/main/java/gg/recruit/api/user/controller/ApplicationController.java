package gg.recruit.api.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.recruit.api.user.controller.response.MyApplicationsResDto;
import gg.recruit.api.user.service.ApplicationService;
import gg.recruit.api.user.service.response.ApplicationListDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitments")
public class ApplicationController {

	private final ApplicationService applicationService;

	@GetMapping("/applications")
	public MyApplicationsResDto getMyApplications(@Login UserDto userDto) {
		ApplicationListDto res = applicationService.findMyApplications(userDto.getId());
		return new MyApplicationsResDto(res);
	}
}
