package gg.recruit.api.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.recruit.api.admin.controller.request.UpdateStatusRequestDto;
import gg.recruit.api.admin.service.AdminRecruitmentService;
import gg.recruit.api.admin.service.UpdateRecruitStatusParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/recruitments")
@RequiredArgsConstructor
public class AdminRecruitmentController {
	private final AdminRecruitmentService adminRecruitmentService;

	@PatchMapping("/{recruitId}/status")
	public ResponseEntity<Void> updateRecruitStatus(@PathVariable Long recruitId,
		@RequestBody UpdateStatusRequestDto requestDto) {

		adminRecruitmentService.updateRecruitStatus(
			new UpdateRecruitStatusParam(recruitId, requestDto.getFinish()));
		return ResponseEntity.noContent().build();
	}
}
