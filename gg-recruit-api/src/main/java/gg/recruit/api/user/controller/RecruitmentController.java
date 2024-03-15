package gg.recruit.api.user.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.recruit.api.user.controller.response.ActiveRecruitmentListResDto;
import gg.recruit.api.user.service.RecruitmentService;
import gg.utils.dto.PageRequestDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitments")
public class RecruitmentController {
	private final RecruitmentService recruitmentService;

	@GetMapping
	public ActiveRecruitmentListResDto findActiveRecruitmentList(PageRequestDto requestDto) {
		Pageable pageable = PageRequest.of(requestDto.getPage() - 1, requestDto.getSize());
		return new ActiveRecruitmentListResDto(recruitmentService.findActiveRecruitmentList(pageable));
	}
}
