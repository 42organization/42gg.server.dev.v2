package gg.recruit.api.admin.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.data.recruit.recruitment.Recruitment;
import gg.recruit.api.admin.controller.request.RecruitmentCreateReqDto;
import gg.recruit.api.admin.service.RecruitmentAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/recruitments")
public class RecruitmentAdminController {
	private final RecruitmentAdminService recruitmentAdminService;

	@PostMapping
	public ResponseEntity<Void> createRecruitment(@RequestBody @Valid RecruitmentCreateReqDto recruitmentDto) {
		Recruitment recruitment = recruitmentDto.toRecruitment();
		recruitmentAdminService.createRecruitment(recruitment, recruitmentDto.getForms());
		return ResponseEntity.ok().build();
	}
}
