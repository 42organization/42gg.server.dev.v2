package gg.pingpong.api.admin.manage.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.pingpong.api.admin.manage.controller.request.PenaltyRequestDto;
import gg.pingpong.api.admin.manage.controller.response.PenaltyListResponseDto;
import gg.pingpong.api.admin.manage.service.PenaltyAdminService;
import gg.pingpong.api.global.dto.PageRequestDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/pingpong/admin/")
public class PenaltyAdminController {
	private final PenaltyAdminService penaltyAdminService;

	@PostMapping("penalty")
	public ResponseEntity givePenaltyToUser(@RequestBody @Valid PenaltyRequestDto requestDto) {
		penaltyAdminService.givePenalty(requestDto.getIntraId(), requestDto.getPenaltyTime(), requestDto.getReason());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("penalty")
	public PenaltyListResponseDto getAllPenaltyUser(@ModelAttribute @Valid PageRequestDto pageRequestDto,
		@RequestParam(required = false) String intraId, @RequestParam Boolean current) {
		Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1, pageRequestDto.getSize(),
			Sort.by("startTime").descending());
		if (intraId == null) {
			return penaltyAdminService.getAllPenalties(pageable, current);
		}
		return penaltyAdminService.getAllPenaltiesByIntraId(pageable, intraId, current);
	}

	@DeleteMapping("penalty/{penaltyId}")
	public ResponseEntity releasePenaltyUser(@PathVariable @Min(1) Long penaltyId) {
		penaltyAdminService.deletePenalty(penaltyId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
