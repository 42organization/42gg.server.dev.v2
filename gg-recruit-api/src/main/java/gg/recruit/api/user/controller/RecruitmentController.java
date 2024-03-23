package gg.recruit.api.user.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import gg.recruit.api.user.controller.response.ActiveRecruitmentListResDto;
import gg.recruit.api.user.controller.response.RecruitmentDetailResDto;
import gg.recruit.api.user.service.ApplicationService;
import gg.recruit.api.user.service.RecruitmentService;
import gg.utils.dto.PageRequestDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitments")
public class RecruitmentController {
	private final RecruitmentService recruitmentService;
	private final ApplicationService applicationService;

	@GetMapping
	public ActiveRecruitmentListResDto findActiveRecruitmentList(PageRequestDto requestDto) {
		Pageable pageable = PageRequest.of(requestDto.getPage() - 1, requestDto.getSize());
		return new ActiveRecruitmentListResDto(recruitmentService.findActiveRecruitmentList(pageable));
	}

	@GetMapping("/{recruitmentId}")
	public RecruitmentDetailResDto findRecruitmentDetail(@Parameter(hidden = true) @Login UserDto user,
		@PathVariable Long recruitmentId) {
		return new RecruitmentDetailResDto(recruitmentService.findRecruitmentDetail(recruitmentId),
			applicationService.findApplicationByUserAndRecruit(user.getId(), recruitmentId));
	}
}
