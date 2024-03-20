package gg.recruit.api.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.recruit.api.user.controller.request.RecruitApplyFormListReqDto;
import gg.recruit.api.user.controller.response.ApplicationResultResDto;
import gg.recruit.api.user.controller.response.MyApplicationDetailResDto;
import gg.recruit.api.user.controller.response.MyApplicationsResDto;
import gg.recruit.api.user.service.ApplicationService;
import gg.recruit.api.user.service.param.DelApplicationParam;
import gg.recruit.api.user.service.param.FindApplicationDetailParam;
import gg.recruit.api.user.service.param.FindApplicationResultParam;
import gg.recruit.api.user.service.param.RecruitApplyFormParam;
import gg.recruit.api.user.service.param.RecruitApplyParam;
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
		@PathVariable Long recruitmentId, @PathVariable Long applicationId) {
		ApplicationWithAnswerSvcDto res = applicationService
			.findMyApplicationDetail(new FindApplicationDetailParam(userDto.getId(), recruitmentId, applicationId));
		return new MyApplicationDetailResDto(res);
	}

	@PostMapping("{recruitmentId}/applications")
	public ResponseEntity<Long> apply(@Login @Parameter(hidden = true) UserDto userDto,
		@PathVariable Long recruitmentId, @RequestBody RecruitApplyFormListReqDto reqDto) {
		List<RecruitApplyFormParam> forms = reqDto.getForms().stream()
			.map(form -> new RecruitApplyFormParam(form.getQuestionId(), form.getInputType(), form.getCheckedList(),
				form.getAnswer()))
			.collect(Collectors.toList());
		Long applicationId = applicationService.recruitApply(
			new RecruitApplyParam(userDto.getId(), recruitmentId, forms));
		return ResponseEntity.status(HttpStatus.CREATED).body(applicationId);
	}

	@GetMapping("{recruitmentId}/applications/{applicationId}/result")
	public ApplicationResultResDto getApplicationResult(@Login @Parameter(hidden = true) UserDto userDto,
		@PathVariable Long recruitmentId, @PathVariable Long applicationId) {
		ApplicationResultSvcDto res = applicationService.findApplicationResult(
			new FindApplicationResultParam(userDto.getId(),
				recruitmentId, applicationId));
		return new ApplicationResultResDto(res);
	}

	@DeleteMapping("/{recruitmentId}/applications/{applicationId}")
	public ResponseEntity<Void> cancelApplication(@Login @Parameter(hidden = true) UserDto userDto,
		@PathVariable Long recruitmentId, @PathVariable Long applicationId) {
		applicationService.deleteApplication(new DelApplicationParam(userDto.getId(), recruitmentId, applicationId));
		return ResponseEntity.noContent().build();
	}
}
