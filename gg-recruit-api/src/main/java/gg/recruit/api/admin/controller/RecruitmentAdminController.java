package gg.recruit.api.admin.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.data.recruit.application.Application;
import gg.data.recruit.recruitment.Recruitment;
import gg.recruit.api.admin.controller.request.InterviewRequestDto;
import gg.recruit.api.admin.controller.request.RecruitmentRequestDto;
import gg.recruit.api.admin.controller.request.SetFinalApplicationStatusResultReqDto;
import gg.recruit.api.admin.controller.request.UpdateStatusRequestDto;
import gg.recruit.api.admin.controller.response.CreatedRecruitmentResponse;
import gg.recruit.api.admin.controller.response.GetRecruitmentApplicationResponseDto;
import gg.recruit.api.admin.controller.response.RecruitmentAdminDetailResDto;
import gg.recruit.api.admin.controller.response.RecruitmentApplicantResultResponseDto;
import gg.recruit.api.admin.controller.response.RecruitmentApplicantResultsResponseDto;
import gg.recruit.api.admin.controller.response.RecruitmentsResponse;
import gg.recruit.api.admin.service.RecruitmentAdminService;
import gg.recruit.api.admin.service.param.GetRecruitmentApplicationsParam;
import gg.recruit.api.admin.service.param.UpdateApplicationStatusParam;
import gg.recruit.api.admin.service.param.UpdateRecruitStatusParam;
import gg.recruit.api.admin.service.result.AllRecruitmentsResult;
import gg.utils.dto.PageRequestDto;
import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/admin/recruitments")
public class RecruitmentAdminController {
	private final RecruitmentAdminService recruitmentAdminService;

	@PostMapping
	public ResponseEntity<CreatedRecruitmentResponse> createRecruitment(
		@RequestBody @Valid RecruitmentRequestDto recruitmentDto) {
		Recruitment recruitment = RecruitmentRequestDto.RecruitmentMapper.INSTANCE.dtoToEntity(recruitmentDto);
		Long recruitmentId = recruitmentAdminService.createRecruitment(recruitment, recruitmentDto.getForms()).getId();
		CreatedRecruitmentResponse createdRecruitmentResponse = new CreatedRecruitmentResponse(recruitmentId);

		return ResponseEntity.status(HttpStatus.CREATED).body(createdRecruitmentResponse);
	}

	@GetMapping("/{recruitId}")
	public RecruitmentAdminDetailResDto findRecruitmentDetail(@PathVariable Long recruitId) {
		RecruitmentAdminDetailResDto res = new RecruitmentAdminDetailResDto(
			recruitmentAdminService.findRecruitmentDetail(recruitId));
		return res;
	}

	@PatchMapping("/{recruitId}/status")
	public ResponseEntity<Void> updateRecruitStatus(@PathVariable @Positive Long recruitId,
		@RequestBody UpdateStatusRequestDto requestDto) {

		recruitmentAdminService.updateRecruitStatus(
			new UpdateRecruitStatusParam(recruitId, requestDto.getFinish()));
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<RecruitmentsResponse> getRecruitments(@Valid PageRequestDto pageRequestDto) {
		Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1, pageRequestDto.getSize());
		AllRecruitmentsResult allRecruitments = recruitmentAdminService.getAllRecruitments(pageable);
		return ResponseEntity.ok(
			new RecruitmentsResponse(allRecruitments.getAllRecruitments(), allRecruitments.getTotalPage()));
	}

	/**
	 * 지원서의 최종 결과를 등록
	 * @return ResponseEntity<Void>
	 */
	@PostMapping("/{recruitId}/result")
	public ResponseEntity<Void> setFinalApplicationStatusResult(
		@PathVariable @Positive Long recruitId,
		@RequestParam("application") @Positive Long applicationId,
		@RequestBody @Valid SetFinalApplicationStatusResultReqDto reqDto) {
		UpdateApplicationStatusParam dto = new UpdateApplicationStatusParam(reqDto.getStatus(), applicationId,
			recruitId);
		recruitmentAdminService.updateFinalApplicationStatusAndNotification(dto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/{recruitId}/applications")
	public ResponseEntity<GetRecruitmentApplicationResponseDto> getRecruitmentApplications(
		@PathVariable @Positive Long recruitId,
		@RequestParam(value = "question", required = false) Long questionId,
		@RequestParam(value = "checks", required = false) String checks,
		@RequestParam(value = "search", required = false) String search,
		@PageableDefault(sort = "id", page = 1) Pageable page
	) {
		GetRecruitmentApplicationsParam dto;

		Pageable parsedPage = PageRequest.of(page.getPageNumber() - 1, Math.min(page.getPageSize(), 20));
		List<Long> checkListIds = parseChecks(checks);
		dto = new GetRecruitmentApplicationsParam(recruitId, questionId, checkListIds, search, parsedPage);
		Page<Application> applicationsPage = recruitmentAdminService.findApplicationsWithAnswersAndUserWithFilter(dto);
		return ResponseEntity.ok(GetRecruitmentApplicationResponseDto.applicationsPageToDto(applicationsPage));
	}

	/**
	 * 전달된 checkListId 목록 파싱
	 * @param checks
	 * @return List<Long>
	 */
	private List<Long> parseChecks(String checks) {
		try {
			if (checks != null) {
				return Arrays.stream(checks.split(",")).map(Long::parseLong).collect(Collectors.toList());
			} else {
				return new ArrayList<>();
			}
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.BAD_ARGU);
		}
	}

	@DeleteMapping("/{recruitId}")
	public ResponseEntity<Void> deleteRecruitment(@PathVariable @Positive Long recruitId) {
		recruitmentAdminService.deleteRecruitment(recruitId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{recruitId}")
	public ResponseEntity<Void> updateRecruitment(@PathVariable @Positive Long recruitId,
		@RequestBody @Valid RecruitmentRequestDto recruitmentDto) {
		Recruitment recruitment = RecruitmentRequestDto.RecruitmentMapper.INSTANCE.dtoToEntity(recruitmentDto);
		recruitment = recruitmentAdminService.updateRecruitment(recruitId, recruitment, recruitmentDto.getForms());
		recruitmentAdminService.createRecruitment(recruitment, recruitmentDto.getForms());
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{recruitId}/interview")
	public ResponseEntity<Void> setInterviewDate(@PathVariable @Positive Long recruitId,
		@RequestParam("application") @Positive Long applicationId,
		@RequestBody @Valid InterviewRequestDto reqDto) {

		recruitmentAdminService.updateDocumentScreening(
			new UpdateApplicationStatusParam(reqDto.getStatus(), applicationId, recruitId, reqDto.getInterviewDate()));
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 모집글 지원서들의 결과 정보 목록 조회
	 * @param recruitId
	 * @return
	 */
	@GetMapping("/{recruitId}/applicants")
	public ResponseEntity<RecruitmentApplicantResultsResponseDto> getRecruitmentApplicantResults(
		@PathVariable @Positive Long recruitId) {
		List<Application> resultApplications = recruitmentAdminService.getRecruitmentApplicants(recruitId);

		List<RecruitmentApplicantResultResponseDto> resultDto = resultApplications.stream()
			.map(RecruitmentApplicantResultResponseDto.MapStruct.INSTANCE::entityToDto)
			.collect(Collectors.toList());

		return ResponseEntity.ok(new RecruitmentApplicantResultsResponseDto(resultDto));
	}
}
