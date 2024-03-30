package gg.recruit.api.admin.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.data.recruit.recruitment.Recruitment;
import gg.recruit.api.admin.controller.request.RecruitmentCreateReqDto;
import gg.recruit.api.admin.controller.request.UpdateStatusRequestDto;
import gg.recruit.api.admin.controller.response.CreatedRecruitmentResponse;
import gg.recruit.api.admin.controller.response.RecruitmentsResponse;
import gg.recruit.api.admin.service.RecruitmentAdminService;
import gg.recruit.api.admin.service.dto.UpdateRecruitStatusParam;
import gg.utils.dto.PageRequestDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/recruitments")
public class RecruitmentAdminController {
	private final RecruitmentAdminService recruitmentAdminService;

	@PostMapping
	public ResponseEntity<CreatedRecruitmentResponse> createRecruitment(
		@RequestBody @Valid RecruitmentCreateReqDto recruitmentDto) {
		Recruitment recruitment = recruitmentDto.toRecruitment();
		Long recruitmentId = recruitmentAdminService.createRecruitment(recruitment, recruitmentDto.getForm()).getId();
		CreatedRecruitmentResponse createdRecruitmentResponse = new CreatedRecruitmentResponse(recruitmentId);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdRecruitmentResponse);
	}

	@PatchMapping("/{recruitId}/status")
	public ResponseEntity<Void> updateRecruitStatus(@PathVariable Long recruitId,
		@RequestBody UpdateStatusRequestDto requestDto) {

		recruitmentAdminService.updateRecruitStatus(
			new UpdateRecruitStatusParam(recruitId, requestDto.getFinish()));
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<RecruitmentsResponse> getRecruitments(PageRequestDto pageRequestDto) {
		Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1, pageRequestDto.getSize());
		List<Recruitment> recruitments = recruitmentAdminService.getAllRecruitments(pageable);
		return ResponseEntity.ok(new RecruitmentsResponse(recruitments));
	}
}
