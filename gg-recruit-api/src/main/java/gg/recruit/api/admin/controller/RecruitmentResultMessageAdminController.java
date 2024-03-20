package gg.recruit.api.admin.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.recruit.api.admin.service.RecruitmentResultMessageAdminService;
import gg.recruit.api.admin.service.dto.RecruitmentResultMessageDto;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/recruitments/result/message")
public class RecruitmentResultMessageAdminController {

	private final RecruitmentResultMessageAdminService resultMessageAdminService;

	@PostMapping
	public ResponseEntity<Void> postResultMessage(@Valid @RequestBody RecruitmentResultMessageDto reqDto) {
		resultMessageAdminService.postResultMessage(reqDto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}
