package gg.party.api.admin.penalty.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gg.party.api.admin.penalty.request.PartyPenaltyAdminReqDto;
import gg.party.api.admin.penalty.service.PartyPenaltyAdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/party/admin/penalties")
public class PartyPenaltyAdminController {
	private final PartyPenaltyAdminService partyPenaltyAdminService;

	/**
	 * 패널티 부여
	 */
	@PostMapping()
	public ResponseEntity<Void> addAdminPenalty(@RequestBody PartyPenaltyAdminReqDto reqDto) {
		partyPenaltyAdminService.addAdminPenalty(reqDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 패널티 수정
	 *
	 * @param penaltyId 패널티 id
	 */
	@PatchMapping("/{penaltyId}")
	public ResponseEntity<Void> modifyAdminPenalty(@PathVariable Long penaltyId,
		@RequestBody PartyPenaltyAdminReqDto reqDto) {
		partyPenaltyAdminService.modifyAdminPenalty(penaltyId, reqDto);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
