package gg.party.api.admin.penalty.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.admin.penalty.controller.request.PageReqDto;
import gg.party.api.admin.penalty.controller.response.PartyPenaltyListAdminResDto;
import gg.party.api.admin.penalty.service.PartyPenaltyAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/admin/penalties")
public class PartyPenaltyAdminController {
	private final PartyPenaltyAdminService penaltyAdminService;

	/**
	 * 패널티 조회
	 * @param reqDto page size
	 * @return PenaltyListAdminResponseDto penaltyList totalPage
	 */
	@GetMapping
	public ResponseEntity<PartyPenaltyListAdminResDto> penaltyList(@ModelAttribute @Valid PageReqDto reqDto) {
		return ResponseEntity.status(HttpStatus.OK).body(penaltyAdminService.findAllPenalty(reqDto));
	}

}
