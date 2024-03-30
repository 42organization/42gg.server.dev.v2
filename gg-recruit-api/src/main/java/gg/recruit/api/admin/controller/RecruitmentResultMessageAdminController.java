package gg.recruit.api.admin.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.data.recruit.manage.ResultMessage;
import gg.recruit.api.admin.controller.response.GetRecruitmentResultMessageResponseDto;
import gg.recruit.api.admin.controller.response.GetRecruitmentResultMessageResponseDtoMapper;
import gg.recruit.api.admin.controller.response.GetRecruitmentResultMessagesResponseDto;
import gg.recruit.api.admin.service.RecruitmentResultMessageAdminService;
import gg.recruit.api.admin.service.dto.RecruitmentResultMessageDto;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/recruitments/result/message")
public class RecruitmentResultMessageAdminController {

	private final RecruitmentResultMessageAdminService resultMessageAdminService;

	/**
	 * 지원자에게 보여질 결과 메시지 등록
	 * @param reqDto
	 * @return ResponseEntity
	 */
	@PostMapping
	public ResponseEntity<Void> postResultMessage(@Valid @RequestBody RecruitmentResultMessageDto reqDto) {
		resultMessageAdminService.postResultMessage(reqDto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * 지원자에게 보여질 결과 메시지 전체 목록 조회
	 * @return ResponseEntity<GetRecruitmentResultMessagesResponseDto>
	 */
	@GetMapping
	public ResponseEntity<GetRecruitmentResultMessagesResponseDto> getResultMessages() {
		List<ResultMessage> resultMessages = resultMessageAdminService.getResultMessages();

		List<GetRecruitmentResultMessageResponseDto> resultDto = resultMessages.stream()
			.map(GetRecruitmentResultMessageResponseDtoMapper.INSTANCE::entityToDto)
			.collect(Collectors.toList());

		return ResponseEntity.ok(new GetRecruitmentResultMessagesResponseDto(resultDto));
	}
}
